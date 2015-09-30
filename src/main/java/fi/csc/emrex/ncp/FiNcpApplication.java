package fi.csc.emrex.ncp;

import org.apache.commons.io.IOUtils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FiNcpApplication {

    private static final String ELMO_XML_FIN = "src/main/resources/Example-elmo-Finland.xml";
    private static final String ELMO_XML_NOR = "src/main/resources/Example-elmo-Norway.xml";
    private static final String ELMO_XML_FIN_URL = "https://raw.githubusercontent.com/EMREXEU/fi-ncp/master/src/main/resources/Example-elmo-Finland.xml";

    public static String getElmo() throws Exception {
        return new String(Files.readAllBytes(Paths.get(new File(ELMO_XML_FIN).getAbsolutePath())));
    }

    public static String getElmoRemote() throws Exception {
        URL url = new URL(ELMO_XML_FIN_URL);

        InputStream stream = url.openStream();
        String elmo;
        try {
            elmo = IOUtils.toString(stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        System.out.println(elmo);
        return elmo;
    }

    public static void main(String[] args) {
        SpringApplication.run(FiNcpApplication.class, args);
    }
}
