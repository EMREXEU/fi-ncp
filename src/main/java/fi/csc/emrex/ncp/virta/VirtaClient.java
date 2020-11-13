package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.util.DateConverter;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihto;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoRequest;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoResponse;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoService;
import fi.csc.tietovaranto.emrex.Hakuehdot;
import fi.csc.tietovaranto.emrex.Kutsuja;
import fi.csc.tietovaranto.emrex.ObjectFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
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

  private static final String AVAIN = "salaisuus";
  private static final String JARJESTELMA = "Emrex";
  private static final String TUNNUS = "Test";

  // Setter only for test purposes: mock this member
  @Setter
  private ELMOOpiskelijavaihtoService elmoOpiskelijavaihtoService;

  @Value("${ncp.virta.url}")
  private String virtaUrl;

  public String fetchStudies(VirtaUserDto virtaUser) throws NpcException {
    try {
      ELMOOpiskelijavaihtoResponse response = sendRequest(virtaUser);
      return VirtaMarshaller.marshal(response);
    } catch (Exception e) {
      throw new NpcException("Fetching studies from VIRTA failed, virta URL:" + virtaUrl, e);
    }
  }

  private ELMOOpiskelijavaihtoResponse sendRequest(VirtaUserDto virtaUser)
      throws MalformedURLException {
    ELMOOpiskelijavaihtoRequest request = createRequest(virtaUser);
    ELMOOpiskelijavaihtoService wsClient = getService();
    ELMOOpiskelijavaihto ws = wsClient.getELMOOpiskelijavaihtoSoap11();

    // TODO: this throws com.sun.xml.ws.fault.ServerSOAPFaultException: Client received SOAP Fault from server: Access denied!
    ELMOOpiskelijavaihtoResponse res = ws.elmoOpiskelijavaihto(request);
    return res;
  }

  private ELMOOpiskelijavaihtoService getService() throws MalformedURLException {
    if (elmoOpiskelijavaihtoService == null) {
      elmoOpiskelijavaihtoService = new ELMOOpiskelijavaihtoService(new URL(virtaUrl));
    }
    return elmoOpiskelijavaihtoService;
  }

  private ELMOOpiskelijavaihtoRequest createRequest(VirtaUserDto virtaUser) {
    ELMOOpiskelijavaihtoRequest request = new ELMOOpiskelijavaihtoRequest();
    request.setKutsuja(getKutsuja());
    request.setHakuehdot(getHakuehdot(virtaUser));
    return request;
  }

  private Hakuehdot getHakuehdot(VirtaUserDto virtaUser) {
    Hakuehdot hakuehdot = new Hakuehdot();
    if (virtaUser.isOidSet()) {
      hakuehdot.getContent().add(0, new ObjectFactory().createOID(virtaUser.getOid()));
    } else {
      hakuehdot.getContent().add(0, new ObjectFactory().createHeTu(virtaUser.getSsn()));
    }

    return hakuehdot;
  }

  private XMLGregorianCalendar convert(LocalDate date) {
    try {
      return DateConverter.convertLocalDateToXmlGregorianCalendar(date);
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private Kutsuja getKutsuja() {
    Kutsuja kutsuja = new Kutsuja();
    kutsuja.setAvain(AVAIN);
    kutsuja.setJarjestelma(JARJESTELMA);
    kutsuja.setTunnus(TUNNUS);
    return kutsuja;
  }

}
