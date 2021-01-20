curl \
  --header "Content-Type: text/xml;charset=UTF-8" \
  --header "SOAPAction: OpintosuorituksetRequest" \
  --data "@virtareq.xml" \
  http://virtawstesti.csc.fi/luku/OpiskelijanTiedot
