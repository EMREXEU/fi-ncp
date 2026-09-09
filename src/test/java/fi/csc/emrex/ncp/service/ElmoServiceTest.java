package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.emrex.ncp.util.FidUtil;
import fi.csc.emrex.ncp.util.GzipUtil;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xml.sax.SAXException;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class ElmoServiceTest {

  @Autowired
  ElmoService elmoService;
  @Autowired
  VirtaClient virtaClient;

  private static Path workingDir;
  private static DataSignService instance;

  @BeforeAll
  static public void init() {
    workingDir = Path.of("", "src/test/resources");
  }

  @Test
  public void convertToElmoXml() throws SAXException, NcpException, jakarta.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient.fetchStudiesAndLearnerDetails(student);
    // log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();

    Elmo elmoXml = elmoService.convertToElmoXml(courses, courses, student,
        createLearnerDetails(opintosuorituksetResponse));

    // log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);

    assertNotNull(elmoXml.getLearner());
    assertEquals("Teppo", elmoXml.getLearner().getGivenNames());
    assertEquals("Testaaja", elmoXml.getLearner().getFamilyName());
    assertNotNull(elmoXml.getLearner().getAlternativeName());
    assertEquals("Teppo Testaaja", elmoXml.getLearner().getAlternativeName().getValue());
    assertFalse(elmoXml.getLearner().getIdentifier().isEmpty());

    assertEquals(1, elmoXml.getReport().size());
    assertNotNull(elmoXml.getReport().get(0).getIssuer());
    assertEquals(courses.size(), elmoXml.getReport().get(0).getLearningOpportunitySpecification().size());
  }

  @Test
  public void convertToElmoXmlSignAndValidate() throws SAXException, NcpException, jakarta.xml.bind.JAXBException, IOException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient.fetchStudiesAndLearnerDetails(student);

    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();

    Elmo elmoXml = elmoService.convertToElmoXml(courses, courses, student,
        createLearnerDetails(opintosuorituksetResponse));

    instance = new DataSignService();
    instance.setCertificatePath("certs/ncp_dev_cert.cer");
    instance.setEncryptionKeyPath("certs/ncp.dev.key");

    final String result = instance.sign(XmlUtil.toString(elmoXml).trim(), StandardCharsets.UTF_8);
    assertFalse(result.isBlank());

    final byte[] decoded = DatatypeConverter.parseBase64Binary(result);
    assertNotNull(decoded);
    assertTrue(decoded.length > 0);

    final byte[] decompressed = GzipUtil.gzipDecompressBytes(decoded);
    assertNotNull(decompressed);
    assertTrue(decompressed.length > 0);

    final String s = new String(decompressed);

    log.info("{}", s);

    // The signed and gzip-round-tripped payload should still carry the learner data and a signature.
    assertTrue(s.contains(elmoXml.getLearner().getGivenNames()));
    assertTrue(s.contains("<Signature"));

    /* //Parser that produces DOM object trees from XML content
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    //API to obtain DOM Document instance
    DocumentBuilder builder = null;
    try
    {
        //Create DocumentBuilder with default configuration
        builder = factory.newDocumentBuilder();

        //Parse the content to Document object
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return null; */

    validateElmoXml(elmoXml);
  }

  @Test
  public void convertToElmoXmlSelectOneReportUseFile()
      throws SAXException, NcpException, jakarta.xml.bind.JAXBException, IOException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = readFile();
    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();

    List<OpintosuoritusTyyppi> filtered = elmoService.trimToSelectedCourses(courses, Arrays.asList("TUTKINTO-39525"));
    // log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    assertEquals(1, filtered.size());
    assertEquals("TUTKINTO-39525", filtered.get(0).getAvain());
    assertTrue(filtered.size() < courses.size());

    Elmo elmoXml = elmoService.convertToElmoXml(filtered, courses, student,
        createLearnerDetails(opintosuorituksetResponse));
    // log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);

    assertEquals(1, elmoXml.getReport().size());
    assertEquals(filtered.size(), elmoXml.getReport().get(0).getLearningOpportunitySpecification().size());
  }

  @Test
  public void convertToElmoXmlSelectOneReportUseVirtaClient()
      throws SAXException, NcpException, jakarta.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient.fetchStudiesAndLearnerDetails(student);
    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();
    List<OpintosuoritusTyyppi> filtered = elmoService.trimToSelectedCourses(courses, Arrays.asList("1451865"));
    // log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    assertFalse(filtered.isEmpty());
    filtered.forEach(course -> assertEquals("1451865", course.getAvain()));
    assertTrue(filtered.size() < courses.size());

    Elmo elmoXml = elmoService.convertToElmoXml(filtered, courses, student,
        createLearnerDetails(opintosuorituksetResponse));
    // log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
    validateElmoXml(elmoXml);

    assertEquals(1, elmoXml.getReport().size());
    assertEquals(filtered.size(), elmoXml.getReport().get(0).getLearningOpportunitySpecification().size());
  }

  /**
   * Schema change under test (this branch): Learner/alternativeName went from required to
   * {@code minOccurs="0"}. A document that omits it must still validate against schema.xsd.
   */
  @Test
  public void schemaAllowsMissingLearnerAlternativeName()
      throws SAXException, NcpException, jakarta.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient.fetchStudiesAndLearnerDetails(student);
    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();

    Elmo elmoXml = elmoService.convertToElmoXml(courses, courses, student,
        createLearnerDetails(opintosuorituksetResponse));

    elmoXml.getLearner().setAlternativeName(null);
    assertFalse(elementIsPresent(XmlUtil.toString(elmoXml), "alternativeName"));

    // Would throw org.xml.sax.SAXParseException against the pre-branch schema, where this element was required.
    validateElmoXml(elmoXml);
  }

  /**
   * Schema change under test (this branch): Issuer/country/@type dropped {@code use="required"} -
   * an absent type now defaults to ISO-3166-alpha-2. A document that omits it must still validate.
   */
  @Test
  public void schemaAllowsMissingIssuerCountryType()
      throws SAXException, NcpException, jakarta.xml.bind.JAXBException {

    VirtaUserDto student = createStudent();
    OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse = virtaClient.fetchStudiesAndLearnerDetails(student);
    List<OpintosuoritusTyyppi> courses = opintosuorituksetResponse.getVirta().getOpiskelija().get(0)
        .getOpintosuoritukset().getOpintosuoritus();

    Elmo elmoXml = elmoService.convertToElmoXml(courses, courses, student,
        createLearnerDetails(opintosuorituksetResponse));

    elmoXml.getReport().get(0).getIssuer().getCountry().setType(null);
    assertFalse(attributeIsPresent(XmlUtil.toString(elmoXml), "country", "type"));

    // Would throw org.xml.sax.SAXParseException against the pre-branch schema, where type was use="required".
    validateElmoXml(elmoXml);
  }

  /** True if the marshalled XML contains an opening tag for {@code elementName} (self- or non-self-closing). */
  private boolean elementIsPresent(String xml, String elementName) {
    return xml.matches("(?s).*<" + elementName + "(\\s[^>]*)?/?>.*");
  }

  /** True if {@code elementName}'s opening tag in the marshalled XML carries an {@code attributeName} attribute. */
  private boolean attributeIsPresent(String xml, String elementName, String attributeName) {
    int start = xml.indexOf("<" + elementName);
    assertTrue(start >= 0, "<" + elementName + "> not found in marshalled XML");
    int end = xml.indexOf('>', start);
    return xml.substring(start, end).contains(attributeName + "=");
  }

  private LearnerDetailsDto createLearnerDetails(OpiskelijanKaikkiTiedotResponse opintosuorituksetResponse)
      throws NcpException {
    // TODO
    LearnerDetailsDto learnerDetails = new LearnerDetailsDto();
    // learnerDetails.setCitizenship(CountryCode.FI);
    learnerDetails.setGivenNames("Teppo");
    learnerDetails.setFamilyName("Testaaja");
    learnerDetails.setAlternateName("Teppo Testaaja");
    learnerDetails.setBday(FidUtil.resolveBirthDate(null, null, opintosuorituksetResponse));
    return learnerDetails;
  }

  private void validateElmoXml(Elmo elmoXml) throws SAXException, jakarta.xml.bind.JAXBException {
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(Path.of("", "src/main/resources/elmo/schema.xsd").toFile());
    StringWriter writer = new StringWriter();
    // BE CAREFUL! Project has two JAXB modules. Use javax.xml.bind.
    JAXBContext jc = JAXBContext.newInstance(Elmo.class);
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setSchema(schema);
    marshaller.marshal(elmoXml, writer);

    // log.info("Validated ELMO XML:\n{}", writer.toString());
  }

  private OpiskelijanKaikkiTiedotResponse readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpiskelijanKaikkiTiedotResponse.xml");
    // log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpiskelijanKaikkiTiedotResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    // unmarshaller.setSchema(getSchema());
    OpiskelijanKaikkiTiedotResponse OpiskelijanKaikkiTiedotResponse = (OpiskelijanKaikkiTiedotResponse) unmarshaller
        .unmarshal(path.toFile());
    return OpiskelijanKaikkiTiedotResponse;
  }

  private OpintosuorituksetResponse _readFile() throws IOException, JAXBException {
    // This is manually chopped XML from actual VIRTA SOAP message.
    Path path = workingDir.resolve("virta_xml/OpitosuoritukseResponse.xml");
    // log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpintosuorituksetResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    // unmarshaller.setSchema(getSchema());
    OpintosuorituksetResponse opintosuorituksetResponse = (OpintosuorituksetResponse) unmarshaller
        .unmarshal(path.toFile());
    return opintosuorituksetResponse;
  }

  /**
   * Use if need schema validation for VIRTA XML. Still missing parts of schema
   * definition chain?
   */
  private Schema getSchema() throws SAXException {
    Source[] schemas = { new StreamSource(workingDir.resolve("virta_xml/Virta.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/wsdl.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/opiskelijatiedot.wsdl").toFile()) };
    return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemas);
  }

  private VirtaUserDto createStudent() {
    return new VirtaUserDto(null, "180766-2213", null);
  }
}
