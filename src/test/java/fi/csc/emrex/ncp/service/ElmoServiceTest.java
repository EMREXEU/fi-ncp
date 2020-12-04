package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
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
  public void convert() throws JAXBException, IOException, SAXException, NpcException {

    VirtaUserDto student = new VirtaUserDto(null, "180766-2213");
    OpintosuorituksetResponse opintosuorituksetResponse = virtaClient.fetchStudies(student);
    log.info("VIRTA XML:\n{}", XmlUtil.toString(opintosuorituksetResponse));

    Elmo elmoXml = elmoService.convertToElmoXml(opintosuorituksetResponse, student);
    validateElmoXml(elmoXml);

    log.info("ELMO XML:\n{}", XmlUtil.toString(elmoXml));
  }

  private void validateElmoXml(Elmo elmoXml) {
    // TODO: validate schema

  }

  private OpintosuorituksetResponse readFile() throws JAXBException, IOException, SAXException {
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
