# EMREX outage page

Instructions for showing `emrex_outage.html` during a service outage on the Apache httpd server (RHEL 9.8). Written for `test.emrex.csc.fi` (`emrex.conf`); the production vhost uses the same approach with its own `ServerName` and `DocumentRoot`.

## How it works

- `emrex_outage.html` is a self-contained page (inline CSS, no external assets) in the Apache docroot, normally `/var/www/html`.
- The vhost contains `IncludeOptional /etc/httpd/maintenance/*.inc`. While no `*.inc` file exists, nothing changes for users.
- Renaming `outage.inc.off` to `outage.inc` and reloading Apache makes every request except the outage page return HTTP **503**, and the outage page is shown as the error document.
- `<Location />` in the vhost requires a Shibboleth login for everything. The switch file exempts `/emrex_outage.html` from auth so visitors see the page instead of the IdP login.

Check the docroot first:

```bash
grep -r DocumentRoot /etc/httpd/conf /etc/httpd/conf.d
```

## One-time setup

### 1. Copy the page to the docroot

```bash
sudo cp emrex_outage.html /var/www/html/
sudo chown root:root /var/www/html/emrex_outage.html
sudo chmod 644 /var/www/html/emrex_outage.html
sudo restorecon -v /var/www/html/emrex_outage.html   # SELinux is enforcing on RHEL 9
```

### 2. Add the include line to the vhost

In the 443 `<VirtualHost>` of `emrex.conf`, as the **last** directive before `</VirtualHost>`. It must come after every `<Location>` block: Apache merges `<Location>` sections in file order, so if the include comes earlier, `<Location />` (Shibboleth) overrides the outage page exemption and the ErrorDocument request gets a `302` to the IdP, which shows up as "a 302 Found error was encountered while trying to use an ErrorDocument" in the 503 response.

```apache
    # Outage switch: active only while a *.inc file exists in this directory
    IncludeOptional /etc/httpd/maintenance/*.inc
```

If the directory does not exist yet, `IncludeOptional` matches nothing, so this is safe to deploy first.

### 3. Create the switch file (disabled)

The directory is outside `conf.d`, so the file is not loaded automatically.

```bash
sudo mkdir -p /etc/httpd/maintenance
sudo tee /etc/httpd/maintenance/outage.inc.off >/dev/null <<'EOF'
<Location /emrex_outage.html>
    AuthType None
    Require all granted
</Location>

ErrorDocument 503 /emrex_outage.html
Header always set Retry-After "3600"

RewriteEngine On
RewriteCond %{REQUEST_URI} !^/emrex_outage\.html$
RewriteRule ^ - [R=503,L]
EOF
```

### 4. Validate and reload

```bash
sudo apachectl configtest && sudo systemctl reload httpd
```

## Running an outage

Before each outage, edit the wording in `emrex_outage.html` ("tänään", "klo 9") and copy it to the docroot again.

The scripts in `scripts/` do the steps below and then run the `curl` checks from [Verify](#verify). They exit non-zero if a check fails. The base URL is a required argument.

```bash
sudo /opt/fi-ncp/scripts/outage-start.sh 'base URL'
sudo /opt/fi-ncp/scripts/outage-stop.sh  'base URL'
```

Or by hand:

### Start

```bash
sudo mv /etc/httpd/maintenance/outage.inc.off /etc/httpd/maintenance/outage.inc
sudo apachectl configtest && sudo systemctl reload httpd
```

### End

```bash
sudo mv /etc/httpd/maintenance/outage.inc /etc/httpd/maintenance/outage.inc.off
sudo apachectl configtest && sudo systemctl reload httpd
```

## Verify

```bash
curl -skI https://test.emrex.csc.fi/ncp/    # expect HTTP 503, no Shibboleth redirect
curl -sk  https://test.emrex.csc.fi/        # expect the outage HTML
```

## Viewing the page without enabling the switch

While the switch is off, `/emrex_outage.html` is covered by the vhost's `<Location />` Shibboleth rule, so:

- **Through the server:** open `https://test.emrex.csc.fi/emrex_outage.html` and log in through Shibboleth. The page is shown after login. This checks file permissions, the SELinux label and the docroot location.
- **Locally:** open `emrex_outage.html` directly in a browser. It is self-contained, so it looks the same. This is the quickest way to check wording and layout.

Neither shows the 503 behaviour; for that the switch must be on (briefly on the test environment, then run the `curl` checks above).

To make the page publicly reachable at all times, add the same `<Location>` exemption to the vhost permanently. This is not recommended, because a stale "tänään" page would be visible to anyone.

## What the switch does to traffic

Not verified on the server yet; check with the `curl` commands above.

**On `test.emrex.csc.fi`, port 443:**

- Every request gets HTTP **503** with the outage page as the body. This covers `/`, `/ncp/...`, `/smp/...`, the auth-exempt URLs (`/test-entry.html`, `/manifest/manifest.xml`, `/ncp/api/error`, etc.) and `/Shibboleth.sso/...`.
- It is not a redirect. The browser keeps the requested URL. Only `/emrex_outage.html` itself is let through.
- The 503 check runs before Shibboleth auth. Nobody is sent to the IdP login, and existing sessions do not help.
- Port 80 still redirects to https first, then gets the 503.

**Not covered:**

- **API and machine clients** get the 503 with an HTML body. This is correct for them, but may show up as errors in their logs (e.g. anyone fetching `manifest.xml`).
- **Other hostnames or vhosts** on the same server are not affected.
- **Direct backend access** (Tomcat on 9090 AJP and 9002) bypasses Apache. If these ports are bound to localhost only, outside users cannot reach them.
- **Users mid-flow** see the 503 on their next request.
- **The backends keep running.** The switch only blocks access through Apache.

## Notes

- `reload` is graceful and does not drop active connections, but users mid-session get the 503 on their next request.
- While the switch is off, `/emrex_outage.html` is not exempt from auth, so it asks for a login and is not publicly visible before the outage.
- 503 tells browsers, monitoring and search engines the outage is temporary. The page also has a `noindex` meta tag.
- The `Location /` block already sends `Cache-Control: no-store`, so clients do not cache the outage page.
- If a CDN or load balancer sits in front of the server, it may need its own maintenance page setting.
- Optionally remove `emrex_outage.html` from the docroot after the outage so a stale "tänään" page is not left on the server.
