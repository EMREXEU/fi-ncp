package fi.csc.emrex.ncp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by marko.hollanti on 04/09/15.
 */
@SpringBootTest
@ActiveProfiles("dev")
public class FiNcpApplicationTest {
  @Test
  public void testGetElmo() throws Exception {

    final String elmoXml = FiNcpApplication.getElmo();
    assertNotNull(elmoXml);
    assertTrue(elmoXml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
  }
}