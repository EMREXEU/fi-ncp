#!/bin/bash
curl --location --request GET 'https://test.emrex.csc.fi/ncp-test/test/ncp_review/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536' \
--header 'Cookie: JSESSIONID=63509369F8C576D9718C3C8C1F69D6AB' \
--data-raw '' \
--verbose
