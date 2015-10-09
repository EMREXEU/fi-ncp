package fi.csc.emrex.ncp.virta;

import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihto;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoRequest;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoResponse;
import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoService;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.time.LocalDate;

/**
 * Created by marko.hollanti on 08/10/15.
 */
public class VirtaClientTest extends TestCase {

    private VirtaClient instance;
    private ELMOOpiskelijavaihtoService elmoOpiskelijavaihtoService;

    public void setUp() throws Exception {
        elmoOpiskelijavaihtoService = Mockito.mock(ELMOOpiskelijavaihtoService.class);
        instance = new VirtaClient();
        instance.setElmoOpiskelijavaihtoService(elmoOpiskelijavaihtoService);
    }

    @Test
    public void testFetchStudies() throws Exception {

        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:ELMOOpiskelijavaihtoResponse xmlns=\"http://purl.org/net/elmo\" xmlns:ns2=\"http://tietovaranto.csc.fi/emrex\"/>\n";

        ELMOOpiskelijavaihto elmoOpiskelijavaihto = Mockito.mock(ELMOOpiskelijavaihto.class);
        Mockito.when(elmoOpiskelijavaihtoService.getELMOOpiskelijavaihtoSoap11()).thenReturn(elmoOpiskelijavaihto);

        ELMOOpiskelijavaihtoResponse elmoOpiskelijavaihtoResponse = new ELMOOpiskelijavaihtoResponse();
        Mockito.when(elmoOpiskelijavaihto.elmoOpiskelijavaihto(Matchers.any(ELMOOpiskelijavaihtoRequest.class))).thenReturn(elmoOpiskelijavaihtoResponse);

        final String result = instance.fetchStudies(createVirtaUser());

        assertEquals(expected, result);
    }

    private VirtaUser createVirtaUser() {
        return new VirtaUser("Kaisa", "Keränen", VirtaUser.Gender.FEMALE, LocalDate.of(1966, 7, 18));
    }

}