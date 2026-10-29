#!/usr/bin/env bash
# Start the EMREX outage: enable the switch file, reload Apache, verify with curl.
# Usage: sudo ./outage-start.sh <base-url>   (e.g. https://test.emrex.csc.fi)
set -euo pipefail

BASE_URL="${1:-}"
DIR=/etc/httpd/maintenance
ON="$DIR/outage.inc"
OFF="$DIR/outage.inc.off"
PAGE_MARKER="EMREXissä on käyttökatko"

die() { echo "ERROR: $*" >&2; exit 1; }

[[ -n $BASE_URL ]] || die "usage: $0 <base-url>, e.g. $0 https://test.emrex.csc.fi"

[[ $EUID -eq 0 ]] || die "run as root (sudo)"
[[ -f /var/www/html/emrex_outage.html ]] || die "/var/www/html/emrex_outage.html missing, copy the page first"

if [[ -f "$ON" ]]; then
    echo "Switch already on ($ON), only verifying."
else
    [[ -f "$OFF" ]] || die "$OFF not found, do the one-time setup first"
    mv "$OFF" "$ON"
    if ! apachectl configtest; then
        mv "$ON" "$OFF"
        die "configtest failed, switch reverted, Apache not reloaded"
    fi
    systemctl reload httpd
    echo "Outage switch enabled, httpd reloaded."
fi

# Give the graceful reload a moment to take effect.
status=""
for _ in 1 2 3 4 5; do
    status=$(curl -sk -o /dev/null -w '%{http_code}' "$BASE_URL/ncp/")
    [[ $status == 503 ]] && break
    sleep 1
done

fail=0
check() { if eval "$2"; then echo "OK   $1"; else echo "FAIL $1"; fail=1; fi; }

check "$BASE_URL/ncp/ returns 503 (got $status)" '[[ $status == 503 ]]'

headers=$(curl -skI "$BASE_URL/ncp/")
check "no redirect to Shibboleth IdP" '! grep -qi "^location:" <<<"$headers"'
check "Retry-After header present" 'grep -qi "^retry-after:" <<<"$headers"'

body=$(curl -sk "$BASE_URL/")
check "$BASE_URL/ shows the outage page" 'grep -q "$PAGE_MARKER" <<<"$body"'

page_status=$(curl -sk -o /dev/null -w '%{http_code}' "$BASE_URL/emrex_outage.html")
check "/emrex_outage.html returns 200 without login (got $page_status)" '[[ $page_status == 200 ]]'

if (( fail )); then
    echo "Outage switch is on, but verification FAILED. Check the output above." >&2
    exit 1
fi
echo "Outage is ON and verified."
