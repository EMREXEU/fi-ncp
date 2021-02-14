package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.tietovaranto.luku.HakuEhdotOrganisaatioVapaa;
import fi.csc.tietovaranto.luku.Kutsuja;
import fi.csc.tietovaranto.luku.OpintosuorituksetRequest;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotRequest;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotRequest;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotRequest.Hakuehdot;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotService;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

  public OpintosuorituksetResponse fetchStudies(VirtaUserDto virtaUser) throws NcpException {
    try {
      return getService().getOpiskelijanTiedotSoap11().opintosuoritukset(
          createRequest(virtaUser));
    } catch (MalformedURLException e) {
      throw new NcpException("Fetching studies from VIRTA failed, virta URL:" + virtaUrl, e);
    }
  }

  public OpiskelijanKaikkiTiedotResponse fetchStudiesAndLearnerDetails(VirtaUserDto virtaUser)
      throws NcpException {
    try {
      return getService().getOpiskelijanTiedotSoap11().opiskelijanKaikkiTiedot(
          createAllDetailsRequest(virtaUser));
    } catch (MalformedURLException e) {
      throw new NcpException("Fetching studies from VIRTA failed, virta URL:" + virtaUrl, e);
    }
  }

  public OpiskelijanTiedotResponse fetchLearnerDetails(VirtaUserDto virtaUser)
      throws NcpException {
    try {
      return getService().getOpiskelijanTiedotSoap11().opiskelijanTiedot(
          createLearnerDetailsRequest(virtaUser));
    } catch (MalformedURLException e) {
      throw new NcpException("Fetching studies from VIRTA failed, virta URL:" + virtaUrl, e);
    }
  }

  private OpiskelijanTiedotRequest createLearnerDetailsRequest(VirtaUserDto virtaUser) {
    OpiskelijanTiedotRequest request = new OpiskelijanTiedotRequest();
    request.setKutsuja(createKutsuja());
    request.setHakuehdot(createLearnerDetailsHakuehdot(virtaUser));
    return request;
  }

  private Hakuehdot createLearnerDetailsHakuehdot(VirtaUserDto virtaUser) {
    Hakuehdot hakuehdot = new Hakuehdot();
    if (virtaUser.isSsnSet()) {
      hakuehdot.setHenkilotunnus(virtaUser.getSsn());
    } else {
      hakuehdot.setKansallinenOppijanumero(virtaUser.getOid());
    }
    return hakuehdot;
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

  private OpiskelijanKaikkiTiedotRequest createAllDetailsRequest(VirtaUserDto virtaUser) {
    OpiskelijanKaikkiTiedotRequest request = new OpiskelijanKaikkiTiedotRequest();
    request.setKutsuja(createKutsuja());
    request.setHakuehdot(createHakuehdot(virtaUser));
    return request;
  }

  private HakuEhdotOrganisaatioVapaa createHakuehdot(VirtaUserDto virtaUser) {
    HakuEhdotOrganisaatioVapaa hakuehdot = new HakuEhdotOrganisaatioVapaa();
    if (virtaUser.isSsnSet()) {
      hakuehdot.setHenkilotunnus(virtaUser.getSsn());
    } else {
      hakuehdot.setKansallinenOppijanumero(virtaUser.getOid());
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
