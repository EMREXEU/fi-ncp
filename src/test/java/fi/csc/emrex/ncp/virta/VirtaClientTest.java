package fi.csc.emrex.ncp.virta;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by marko.hollanti on 08/10/15.
 */
public class VirtaClientTest extends TestCase {

    private VirtaClient instance;

    public void setUp() throws Exception {
        instance = new VirtaClient();
    }

    @Ignore
    @Test
    public void testFetchStudies() throws Exception {

        String xmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:ELMOOpiskelijavaihtoResponse xmlns=\"http://purl.org/net/elmo\" xmlns:ns2=\"http://tietovaranto.csc.fi/emrex\">\n" +
                "    <elmo>\n" +
                "        <report>\n" +
                "            <learner>";

        final String result = instance.fetchStudies(createVirtaUser());

        System.out.println(result);

//        assertTrue(result.startsWith(xmlPrefix));
    }

    @Ignore
    @Test
    public void testFetchStudiesWithInvalidVirtaUser() throws Exception {

        final String result = instance.fetchStudies(createInvalidVirtaUser());
        assertNull(result);

    }

    private VirtaUser createVirtaUser() {
        return new VirtaUser("Kaisa", "Keränen", VirtaUser.Gender.FEMALE, LocalDate.of(1966, 7, 18));
    }

    private VirtaUser createInvalidVirtaUser() {
        return new VirtaUser("Kaisa", "Keränen", VirtaUser.Gender.MALE, LocalDate.of(1966, 7, 18));
    }
}