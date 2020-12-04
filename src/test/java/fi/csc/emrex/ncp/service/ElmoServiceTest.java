package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
//@AutoConfigureMockMvc
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
  public void convert() throws SAXException, NpcException, javax.xml.bind.JAXBException {

    VirtaUserDto student = new VirtaUserDto(null, "180766-2213");
    OpintosuorituksetResponse opintosuorituksetResponse = virtaClient.fetchStudies(student);
    log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    Elmo elmoXml = elmoService.convertToElmoXml(opintosuorituksetResponse, student);
    validateElmoXml(elmoXml);

    log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
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

  private OpintosuorituksetResponse readFile() throws IOException, SAXException, JAXBException {
    Path path = workingDir.resolve("virta_xml/OpitosuoritukseResponse.xml");
    Source[] schemas = {
        new StreamSource(workingDir.resolve("virta_xml/Virta.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/wsdl.xsd").toFile()),
        new StreamSource(workingDir.resolve("virta_xml/opiskelijatiedot.wsdl").toFile())
    };
    log.info("XML file:\n{}", Files.readString(path));
    JAXBContext ctx = JAXBContext.newInstance(OpintosuorituksetResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    unmarshaller.setSchema(
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            .newSchema(schemas));
    OpintosuorituksetResponse opintosuorituksetResponse =
        (OpintosuorituksetResponse) unmarshaller.unmarshal(path.toFile());
    return opintosuorituksetResponse;
  }

}
