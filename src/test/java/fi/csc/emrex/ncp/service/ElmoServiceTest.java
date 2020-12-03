package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@AutoConfigureMockMvc
@Slf4j
public class ElmoServiceTest {

  @Autowired
  ElmoService elmoService;

  private static Path workingDir;

  @BeforeAll
  static public void init() {
    workingDir = Path.of("", "src/test/resources");
  }

  @Test
  public void convert() throws JAXBException, IOException {

    Path path = workingDir.resolve("virta_xml/OpitosuoritukseResponse.xml");
    log.info("XML file:\n{}", Files.readString(path));

    JAXBContext ctx = JAXBContext.newInstance(OpintosuorituksetResponse.class);
    Unmarshaller unmarshaller = ctx.createUnmarshaller();
    OpintosuorituksetResponse opintosuorituksetResponse = (OpintosuorituksetResponse) unmarshaller
        .unmarshal(path.toFile());

    VirtaUserDto student = new VirtaUserDto(null, "180766-2213");
    Elmo elmoXml = elmoService.convertToElmoXml(opintosuorituksetResponse, student);
  }
}
