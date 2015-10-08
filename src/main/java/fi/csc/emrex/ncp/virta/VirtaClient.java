package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.DateConverter;
import fi.csc.tietovaranto.emrex.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.time.LocalDate;

/**
 * Created by marko.hollanti on 28/09/15.
 */
@Slf4j
public class VirtaClient {

    public static final String AVAIN = "salaisuus";
    public static final String JARJESTELMA = "Emrex";
    public static final String TUNNUS = "Test";

    @WebServiceRef
    private ELMOOpiskelijavaihtoService elmoOpiskelijavaihtoService;

    public String fetchStudies(VirtaUser virtaUser) {
        try {
            return marshal(sendRequest(virtaUser));
        } catch (Exception e) {
            log.error("fetchStudies failed", e);
            return null;
        }

    }

    private ELMOOpiskelijavaihtoResponse sendRequest(VirtaUser virtaUser) throws MalformedURLException {
        ELMOOpiskelijavaihtoService service = new ELMOOpiskelijavaihtoService();
        return service.getELMOOpiskelijavaihtoSoap11().elmoOpiskelijavaihto(createRequest(virtaUser));
    }

    private ELMOOpiskelijavaihtoRequest createRequest(VirtaUser virtaUser) {
        ELMOOpiskelijavaihtoRequest request = new ELMOOpiskelijavaihtoRequest();
        request.setKutsuja(luoKutsuja());
        request.setHakuehdot(luoHakuehdot(virtaUser));
        return request;
    }

    private Hakuehdot luoHakuehdot(VirtaUser virtaUser) {
        Hakuehdot hakuehdot = new Hakuehdot();
        hakuehdot.setEtunimi(virtaUser.getFirstName());
        hakuehdot.setSukunimi(virtaUser.getLastName());
        hakuehdot.setSukupuoli(virtaUser.getGender().getValue());
        hakuehdot.setSyntymaaika(convert(virtaUser.getBirthday()));
        return hakuehdot;
    }

    private XMLGregorianCalendar convert(LocalDate date) {
        try {
            return DateConverter.convertLocalDateToXmlGregorianCalendar(date);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Kutsuja luoKutsuja() {
        Kutsuja kutsuja = new Kutsuja();
        kutsuja.setAvain(AVAIN);
        kutsuja.setJarjestelma(JARJESTELMA);
        kutsuja.setTunnus(TUNNUS);
        return kutsuja;
    }

    private String marshal(ELMOOpiskelijavaihtoResponse response) throws JAXBException {
        final Marshaller m = JAXBContext.newInstance(ELMOOpiskelijavaihtoResponse.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final StringWriter sw = new StringWriter();
        m.marshal(response, sw);
        return sw.toString();
    }

}
