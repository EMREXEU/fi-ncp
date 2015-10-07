package fi.csc.emrex.ncp.virta;

import fi.csc.emrex.ncp.DateConverter;
import fi.csc.tietovaranto.emrex.*;

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
public class VirtaClient {

    @WebServiceRef
    private ELMOOpiskelijavaihtoService elmoOpiskelijavaihtoService;

    public static void main(String[] args) throws MalformedURLException, JAXBException {
        VirtaClient v = new VirtaClient();
        System.out.println(v.fetchStudies(new VirtaUser("Kaisa", "Keränen", VirtaUser.Gender.FEMALE, LocalDate.of(1966, 7, 18))));
    }

    public String fetchStudies(VirtaUser virtaUser) throws MalformedURLException, JAXBException {
        return marshal(sendRequest(virtaUser));
    }

    private ELMOOpiskelijavaihtoResponse sendRequest(VirtaUser virtaUser) throws MalformedURLException {
        ELMOOpiskelijavaihtoService service = new ELMOOpiskelijavaihtoService();
        return service.getELMOOpiskelijavaihtoSoap11().elmoOpiskelijavaihto(luoRequest(virtaUser));
    }

    private ELMOOpiskelijavaihtoRequest luoRequest(VirtaUser virtaUser) {
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
        kutsuja.setAvain("salaisuus");
        kutsuja.setJarjestelma("Emrex");
        kutsuja.setTunnus("Test");
        return kutsuja;
    }

    public String marshal(ELMOOpiskelijavaihtoResponse response) throws JAXBException {
        final Marshaller m = JAXBContext.newInstance(ELMOOpiskelijavaihtoResponse.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final StringWriter sw = new StringWriter();
        m.marshal(response, sw);
        return sw.toString();
    }

}
