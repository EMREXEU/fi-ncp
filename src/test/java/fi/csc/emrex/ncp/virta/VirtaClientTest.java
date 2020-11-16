package fi.csc.emrex.ncp.virta;

import fi.csc.tietovaranto.luku.OpintosuorituksetRequest;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedot;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotService;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Created by marko.hollanti on 08/10/15.
 */
public class VirtaClientTest extends TestCase {

  private VirtaClient instance;
  private OpiskelijanTiedotService service;

  public void setUp() throws Exception {
    service = Mockito.mock(OpiskelijanTiedotService.class);
    instance = new VirtaClient();
    instance.setOpiskelijanTiedotService(service);
  }

  @Test
  public void testFetchStudies() throws Exception {

    final String expected = "<ns3:elmo xmlns=\"urn:mace:funet.fi:virta/2015/09/01\" xmlns:ns2=\"http://tietovaranto.csc.fi/luku\" xmlns:ns3=\"http://purl.org/net/elmo\"/>";

    OpiskelijanTiedot ws = Mockito.mock(OpiskelijanTiedot.class);
    Mockito.when(service.getOpiskelijanTiedotSoap11()).thenReturn(ws);

    OpintosuorituksetResponse response = new OpintosuorituksetResponse();
    Mockito.when(
        ws.opintosuoritukset(Matchers.any(OpintosuorituksetRequest.class)))
        .thenReturn(response);

    final String result = instance.fetchStudies(createVirtaUser());

    // TODO: first figure out the required namespace and content before asserting
    assertEquals(expected, result);
  }

  private VirtaUserDto createVirtaUser() {
    return new VirtaUserDto("17488477125", null);
  }

}
