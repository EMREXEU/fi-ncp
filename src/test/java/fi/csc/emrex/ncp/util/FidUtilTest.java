package fi.csc.emrex.ncp.util;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class FidUtilTest {

  private static Path workingDir;

  @BeforeAll
  static public void init() {
    workingDir = Path.of("", "src/test/resources");
  }

  @Test
  public void parseFidFromBday() throws NcpException {
    String shibBday = "19660718";
    String shibUid = "";
    OpiskelijanKaikkiTiedotResponse virtaXml = null;
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }

  @Test
  public void parseFidFromUid() throws NcpException {
    String shibBday = "";
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213";
    OpiskelijanKaikkiTiedotResponse virtaXml = null;
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }

  @Test
  public void parseFidFromVirtaXml() throws NcpException, IOException, JAXBException {
    String shibBday = "";
    String shibUid = "";
    OpiskelijanKaikkiTiedotResponse virtaXml = (readFile());
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }

  private OpiskelijanKaikkiTiedotResponse readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpiskelijanKaikkiTiedotResponse.xml");
    log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpiskelijanKaikkiTiedotResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    OpiskelijanKaikkiTiedotResponse OpiskelijanKaikkiTiedotResponse =
        (OpiskelijanKaikkiTiedotResponse) unmarshaller.unmarshal(path.toFile());
    return OpiskelijanKaikkiTiedotResponse;
  }
}
