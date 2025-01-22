package fi.csc.emrex.ncp.util;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fi.csc.emrex.ncp.util.FidUtil.calculateControlCharacter;
import static fi.csc.emrex.ncp.util.FidUtil.getFid;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
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
    LocalDate cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(Month.JULY, cal.getMonth());
    Assertions.assertEquals(18, cal.getDayOfMonth());
  }

  @Test
  public void parseFidFromUid() throws NcpException {
    String shibBday = "";
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213";
    OpiskelijanKaikkiTiedotResponse virtaXml = null;
    LocalDate cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(Month.JULY, cal.getMonth());
    Assertions.assertEquals(18, cal.getDayOfMonth());
  }

  @Test
  public void parseFidFromVirtaXml() throws NcpException, IOException, JAXBException {
    String shibBday = "";
    String shibUid = "";
    OpiskelijanKaikkiTiedotResponse virtaXml = (readFile());
    LocalDate cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(Month.JULY, cal.getMonth());
    Assertions.assertEquals(18, cal.getDayOfMonth());
  }

  @Test
  public void validHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-911N";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertTrue(isValid);
  }

  @Test
  public void validHetu2() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:131052-308T";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertTrue(isValid);
  }

  @Test
  public void validHetuNewControlChars() {
    String[] validIds = {"010594Y9032", "010594Y9021", "020594X903P", "020594X902N", "030594W903B", "030694W9024", "040594V9030", "040594V902Y", "050594U903M", "050594U902L", "010516B903X", "010516B902W", "020516C903K", "020516C902J", "030516D9037", "030516D9026", "010501E9032", "020502E902X", "020503F9037", "020504A902E", "020504B904H"};
    for (String id : validIds) {
      String fid = getFid(id);
      boolean isValid = FidUtil.isValid(fid);
      Assertions.assertTrue(isValid);
    }
  }

  @Test
  public void invalidHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void invalidHetu2() {
    String shibUid = "qwerty";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void invalidHetu3() {
    String shibUid = "q.-,?=)(/&%";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void invalidContolCharHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766+2213";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void invalidLenHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void emptyHetu() {
    boolean isValid = FidUtil.isValid("");
    Assertions.assertFalse(isValid);
  }

  @Test
  public void nullHetu() {
    Assertions.assertFalse(FidUtil.isValid(null));
  }

  @Test
  public void nonNumericHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:XX0766-2213";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void invalidDateHetu() {
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:000766-2213";
    String fid = getFid(shibUid);
    boolean isValid = FidUtil.isValid(fid);
    Assertions.assertFalse(isValid);
  }

  @Test
  public void calculateControlChar() {
    Assertions.assertEquals('T', calculateControlCharacter(131052308));
  }

  private OpiskelijanKaikkiTiedotResponse readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpiskelijanKaikkiTiedotResponse.xml");
    // log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpiskelijanKaikkiTiedotResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    OpiskelijanKaikkiTiedotResponse OpiskelijanKaikkiTiedotResponse =
        (OpiskelijanKaikkiTiedotResponse) unmarshaller.unmarshal(path.toFile());
    return OpiskelijanKaikkiTiedotResponse;
  }
}
