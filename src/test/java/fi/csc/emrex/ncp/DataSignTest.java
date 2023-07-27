package fi.csc.emrex.ncp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.csc.emrex.ncp.service.DataSignService;
import fi.csc.emrex.ncp.util.GzipUtil;
import fi.csc.emrex.ncp.util.TestUtil;
import java.nio.charset.StandardCharsets;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataSignTest {

  private static DataSignService instance;

  @BeforeAll
  public static void setUp() throws Exception {
    instance = new DataSignService();
    instance.setCertificatePath("certs/ncp_dev_cert.cer");
    instance.setEncryptionKeyPath("certs/ncp.dev.key");
  }

  @Test
  public void testSign() throws Exception {

    final String data = TestUtil.getFileContent("Example-elmo-Finland.xml");

    // System.out.println(data);

    final String result = instance.sign(data, StandardCharsets.UTF_8);

    final byte[] decoded = DatatypeConverter.parseBase64Binary(result);
    assertNotNull(decoded);

    final byte[] decompressed = GzipUtil.gzipDecompressBytes(decoded);
    assertNotNull(decompressed);

    final String s = new String(decompressed);

    assertTrue(s.contains(
        "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>"));
    assertTrue(s.endsWith("</X509Certificate></X509Data></KeyInfo></Signature></elmo>"));

    /* System.out.println("Validate signature");
    try {
      Boolean valid = instance.verifySignature(s);
      System.out.println("XML Signature valid: " + valid);
      assertTrue(valid);
    } catch (Exception e) {
    } */
  }
}
