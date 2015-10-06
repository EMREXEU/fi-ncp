package fi.csc.emrex.ncp;

import fi.csc.emrex.ncp.util.TestUtil;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by marko.hollanti on 06/10/15.
 */
public class DataSignTest extends TestCase {

    private DataSign instance;

    @Before
    public void setUp() {
        instance = new DataSign();
    }

    @Test
    public void testSign() throws Exception {

        final String cert = TestUtil.getFileContent("csc-cert.crt");
        final String key = TestUtil.getFileContent("csc-privkey-pkcs8.key");
        final String data = TestUtil.getFileContent("Example-elmo-Finland.xml");

        final String result = instance.sign(cert, key, data);

        assertTrue(result.contains("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>"));
        assertTrue(result.endsWith("</X509Certificate></X509Data></KeyInfo></Signature></elmo>"));
    }
}