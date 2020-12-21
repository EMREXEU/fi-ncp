package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.util.FidUtil;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

@SpringBootTest
@Slf4j
public class ElmoServiceTest {

  @Autowired
  ElmoService elmoService;
  @Autowired
  VirtaClient virtaClient;

  private static Path workingDir;

  @BeforeAll
  static public void init() {
    workingDir = Path.of("", "src/test/resources");
  }

  @Test
  public void convertToElmoXml() throws SAXException, NpcException, javax.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient
        .fetchStudiesAndLearnerDetails(student);
    log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    Elmo elmoXml = elmoService.convertToElmoXml(
        opintosuorituksetResponse,
        student,
        createLearnerDetails(opintosuorituksetResponse));

    //log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);
  }

  @Test
  public void convertToElmoXmlSelectOneReportUseFile()
      throws SAXException, NpcException, javax.xml.bind.JAXBException, IOException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = readFile();
    opintosuorituksetResponse = elmoService
        .trimToSelectedCourses(opintosuorituksetResponse, Arrays.asList("TUTKINTO-39525"));
    log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    Elmo elmoXml = elmoService.convertToElmoXml(
        opintosuorituksetResponse,
        student,
        createLearnerDetails(opintosuorituksetResponse));
    //log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);
  }

  @Test
  public void convertToElmoXmlSelectOneReportUseVirtaClient()
      throws SAXException, NpcException, javax.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient
        .fetchStudiesAndLearnerDetails(student);
    opintosuorituksetResponse = elmoService
        .trimToSelectedCourses(opintosuorituksetResponse, Arrays.asList("1451865"));
    log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    Elmo elmoXml = elmoService.convertToElmoXml(
        opintosuorituksetResponse,
        student,
        createLearnerDetails(opintosuorituksetResponse));
    //log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);
  }


  private LearnerDetailsDto createLearnerDetails(
      OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse)
      throws NpcException {
    // TODO
    LearnerDetailsDto learnerDetails = new LearnerDetailsDto();
    learnerDetails.setCitizenship(CountryCode.FI);
    learnerDetails.setGivenNames("Teppo");
    learnerDetails.setFamilyName("Testaaja");
    learnerDetails.setBday(FidUtil.resolveBirthDate(null, null, opintosuorituksetResponse));
    return learnerDetails;
  }

  private void validateElmoXml(Elmo elmoXml) throws SAXException, javax.xml.bind.JAXBException {
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(workingDir.resolve("elmo_xml/schema.xsd").toFile());
    StringWriter writer = new StringWriter();
    // BE CAREFUL! Project has two JAXB modules. Use javax.xml.bind.
    JAXBContext jc = JAXBContext.newInstance(Elmo.class);
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setSchema(schema);
    marshaller.marshal(elmoXml, writer);

    log.info("Validated ELMO XML:\n{}", writer.toString());
  }


  private OpiskelijanKaikkiTiedotResponse readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpiskelijanKaikkiTiedotResponse.xml");
    log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpiskelijanKaikkiTiedotResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    //unmarshaller.setSchema(getSchema());
    OpiskelijanKaikkiTiedotResponse OpiskelijanKaikkiTiedotResponse =
        (OpiskelijanKaikkiTiedotResponse) unmarshaller.unmarshal(path.toFile());
    return OpiskelijanKaikkiTiedotResponse;
  }


  private OpintosuorituksetResponse _readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpitosuoritukseResponse.xml");
    log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpintosuorituksetResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    //unmarshaller.setSchema(getSchema());
    OpintosuorituksetResponse opintosuorituksetResponse =
        (OpintosuorituksetResponse) unmarshaller.unmarshal(path.toFile());
    return opintosuorituksetResponse;
  }

  /**
   * Use if need schema validation for VIRTA XML. Still missing parts of schema definition chain?
   */
  private Schema getSchema() throws SAXException {
    Source[] schemas = {
        new StreamSource(workingDir.resolve("virta_xml/Virta.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/wsdl.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/opiskelijatiedot.wsdl").toFile())
    };
    return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .newSchema(schemas);
  }

  private VirtaUserDto createStudent() {
    return  new VirtaUserDto(null, "180766-2213", "02536");
  }
}
