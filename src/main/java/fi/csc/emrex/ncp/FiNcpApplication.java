package fi.csc.emrex.ncp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FiNcpApplication {

  private static final String ELMO_XML_FIN = "src/main/resources/Example-elmo-Finland.xml";
  private static final String ELMO_XML_NOR = "src/main/resources/Example-elmo-Norway.xml";
  private static final String ELMO_XML_FIN_URL = "https://raw.githubusercontent.com/EMREXEU/fi-ncp/master/src/main/resources/Example-elmo-Finland.xml";

  public static String getElmo() throws Exception {
    return new String(Files.readAllBytes(Paths.get(new File(ELMO_XML_FIN).getAbsolutePath())));
  }

  public static void main(String[] args) {
    SpringApplication.run(FiNcpApplication.class, args);
  }
}
