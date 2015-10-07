package fi.csc.emrex.ncp;

import fi.csc.tietovaranto.emrex.*;

import javax.xml.datatype.DatatypeConfigurationException;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * Created by marko.hollanti on 28/09/15.
 */
public class VirtaClient {

    public static void main(String[] args) throws MalformedURLException {
        VirtaClient v = new VirtaClient();
        final ELMOOpiskelijavaihtoResponse response = v.sendRequest();
        final String fullName = response.getElmo().getReport().get(0).getLearner().getFullName().getValue();
        System.out.println(fullName);
    }

    private ELMOOpiskelijavaihtoResponse sendRequest() throws MalformedURLException {
        ELMOOpiskelijavaihtoService service = new ELMOOpiskelijavaihtoService();
        return service.getELMOOpiskelijavaihtoSoap11().elmoOpiskelijavaihto(luoRequest());
    }

    private ELMOOpiskelijavaihtoRequest luoRequest() {
        ELMOOpiskelijavaihtoRequest request = new ELMOOpiskelijavaihtoRequest();

        request.setKutsuja(luoKutsuja());
        request.setHakuehdot(luoHakuehdot());

        return request;
    }

    private Hakuehdot luoHakuehdot() {
        Hakuehdot hakuehdot = new Hakuehdot();
        hakuehdot.setEtunimi("Kaisa");
        hakuehdot.setSukunimi("Keränen");
        hakuehdot.setSukupuoli("Nainen");
        ;
        try {
            hakuehdot.setSyntymaaika(DateConverter.convertStringToXmlGregorianCalendar("18.07.1966", "dd.MM.yyyy"));
        } catch (DatatypeConfigurationException | ParseException e) {
            throw new RuntimeException(e);
        }
        return hakuehdot;
    }

    private Kutsuja luoKutsuja() {
        Kutsuja kutsuja = new Kutsuja();
        kutsuja.setAvain("salaisuus");
        kutsuja.setJarjestelma("Emrex");
        kutsuja.setTunnus("Test");
        return kutsuja;
    }

}
