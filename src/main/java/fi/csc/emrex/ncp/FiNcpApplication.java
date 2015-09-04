package fi.csc.emrex.ncp;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FiNcpApplication {

    private static final String ELMO_XML = "src/main/resources/Example-elmo-Finland.xml";

    public static String getElmo() throws Exception {
        return new String(Files.readAllBytes(Paths.get(new File(ELMO_XML).getAbsolutePath())));
    }

    public static void main(String[] args) {
        SpringApplication.run(FiNcpApplication.class, args);
    }
}
