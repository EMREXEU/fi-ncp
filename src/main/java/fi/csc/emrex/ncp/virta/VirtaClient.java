package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.tietovaranto.luku.HakuEhdotOrganisaatioVapaa;
import fi.csc.tietovaranto.luku.Kutsuja;
import fi.csc.tietovaranto.luku.OpintosuorituksetRequest;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedot;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotService;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by marko.hollanti on 28/09/15.
 */
@Slf4j
@Component
public class VirtaClient {

  @Value("${ncp.virta.url}")
  private String virtaUrl;
  @Value("${ncp.virta.key}")
  private String AVAIN;
  @Value("${ncp.virta.system}")
  private String JARJESTELMA;
  @Value("${ncp.virta.user}")
  private String TUNNUS;

  // Setter only for test purposes: mock this member
  @Setter
  private OpiskelijanTiedotService opiskelijanTiedotService;

  public String fetchStudies(VirtaUserDto virtaUser) throws NpcException {
    try {
      OpintosuorituksetResponse response = sendRequest(virtaUser);
      return VirtaMarshaller.marshal(response);
    } catch (Exception e) {
      throw new NpcException("Fetching studies from VIRTA failed, virta URL:" + virtaUrl, e);
    }
  }

  private OpintosuorituksetResponse sendRequest(VirtaUserDto virtaUser)
      throws MalformedURLException {
    OpiskelijanTiedotService wsClient = getService();
    OpiskelijanTiedot ws = wsClient.getOpiskelijanTiedotSoap11();
    OpintosuorituksetRequest request = createRequest(virtaUser);
    OpintosuorituksetResponse res = ws.opintosuoritukset(request);
    return res;
  }

  private OpiskelijanTiedotService getService() throws MalformedURLException {
    if (opiskelijanTiedotService == null) {
      opiskelijanTiedotService = new OpiskelijanTiedotService(new URL(virtaUrl));
    }
    return opiskelijanTiedotService;
  }

  private OpintosuorituksetRequest createRequest(VirtaUserDto virtaUser) {
    OpintosuorituksetRequest request = new OpintosuorituksetRequest();
    request.setKutsuja(createKutsuja());
    request.setHakuehdot(createHakuehdot(virtaUser));
    return request;
  }

  private HakuEhdotOrganisaatioVapaa createHakuehdot(VirtaUserDto virtaUser) {
    HakuEhdotOrganisaatioVapaa hakuehdot = new HakuEhdotOrganisaatioVapaa();
    if (virtaUser.isOidSet()) {
      hakuehdot.setKansallinenOppijanumero(virtaUser.getOid());
      //hakuehdot.getContent().add(0, new ObjectFactory().createOID(virtaUser.getOid()));
    } else {
      hakuehdot.setHenkilotunnus(virtaUser.getSsn());
      //hakuehdot.getContent().add(0, new ObjectFactory().createHeTu(virtaUser.getSsn()));
    }

    return hakuehdot;
  }

  private Kutsuja createKutsuja() {
    Kutsuja kutsuja = new Kutsuja();
    kutsuja.setAvain(AVAIN);
    kutsuja.setJarjestelma(JARJESTELMA);
    kutsuja.setTunnus(TUNNUS);
    return kutsuja;
  }

}
