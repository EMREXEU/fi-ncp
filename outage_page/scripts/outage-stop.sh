#!/usr/bin/env bash
# Stop the EMREX outage: disable the switch file, reload Apache, verify with curl.
# Usage: sudo ./outage-stop.sh <base-url>   (e.g. https://test.emrex.csc.fi)
set -euo pipefail

BASE_URL="${1:-}"
DIR=/etc/httpd/maintenance
ON="$DIR/outage.inc"
OFF="$DIR/outage.inc.off"
PAGE_MARKER="EMREXissä on käyttökatko"

die() { echo "ERROR: $*" >&2; exit 1; }

[[ -n $BASE_URL ]] || die "usage: $0 <base-url>, e.g. $0 https://test.emrex.csc.fi"

[[ $EUID -eq 0 ]] || die "run as root (sudo)"

if [[ -f "$ON" ]]; then
    [[ ! -e "$OFF" ]] || die "both $ON and $OFF exist, resolve manually"
    mv "$ON" "$OFF"
    if ! apachectl configtest; then
        mv "$OFF" "$ON"
        die "configtest failed, switch reverted, Apache not reloaded (outage still on)"
    fi
    systemctl reload httpd
    echo "Outage switch disabled, httpd reloaded."
else
    echo "Switch already off ($ON not present), only verifying."
fi

# Give the graceful reload a moment to take effect.
status=""
for _ in 1 2 3 4 5; do
    status=$(curl -sk -o /dev/null -w '%{http_code}' "$BASE_URL/ncp/")
    [[ $status != 503 ]] && break
    sleep 1
done

fail=0
check() { if eval "$2"; then echo "OK   $1"; else echo "FAIL $1"; fail=1; fi; }

check "$BASE_URL/ncp/ no longer returns 503 (got $status)" '[[ $status != 503 && $status != 000 ]]'

body=$(curl -sk "$BASE_URL/ncp/")
check "outage page no longer served" '! grep -q "$PAGE_MARKER" <<<"$body"'

page_status=$(curl -sk -o /dev/null -w '%{http_code}' "$BASE_URL/emrex_outage.html")
check "/emrex_outage.html requires login again (got $page_status, expect 302)" '[[ $page_status == 302 ]]'

if (( fail )); then
    echo "Outage switch is off, but verification FAILED. Check the output above." >&2
    exit 1
fi
echo "Outage is OFF and verified."
