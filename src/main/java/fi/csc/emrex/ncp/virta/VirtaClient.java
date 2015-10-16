package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.DateConverter;
import fi.csc.tietovaranto.emrex.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

/**
 * Created by marko.hollanti on 28/09/15.
 */
@Slf4j
@Setter
@Component
public class VirtaClient {

    public static final String AVAIN = "salaisuus";
    public static final String JARJESTELMA = "Emrex";
    public static final String TUNNUS = "Test";

    private ELMOOpiskelijavaihtoService elmoOpiskelijavaihtoService;

    @Value("${ncp.virta.url}")
    private String virtaUrl;

    public String fetchStudies(String oid, String ssn) {
        return fetchStudies(new VirtaUser(oid, ssn));
    }

    public String fetchStudies(VirtaUser virtaUser) {
        try {
            return VirtaMarshaller.marshal(sendRequest(virtaUser));
        } catch (Exception e) {
            log.error("fetchStudies failed", e);
            return null;
        }
    }

    private ELMOOpiskelijavaihtoResponse sendRequest(VirtaUser virtaUser) throws MalformedURLException {
        return getService().getELMOOpiskelijavaihtoSoap11().elmoOpiskelijavaihto(createRequest(virtaUser));
    }

    private ELMOOpiskelijavaihtoService getService() throws MalformedURLException {
        if (elmoOpiskelijavaihtoService == null) {
            elmoOpiskelijavaihtoService = new ELMOOpiskelijavaihtoService(new URL(virtaUrl));
        }
        return elmoOpiskelijavaihtoService;
    }

    private ELMOOpiskelijavaihtoRequest createRequest(VirtaUser virtaUser) {
        ELMOOpiskelijavaihtoRequest request = new ELMOOpiskelijavaihtoRequest();
        request.setKutsuja(getKutsuja());
        request.setHakuehdot(getHakuehdot(virtaUser));
        return request;
    }

    private Hakuehdot getHakuehdot(VirtaUser virtaUser) {
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
