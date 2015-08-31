package fi.csc.emrex.ncp;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication

public class FiNcpApplication {



    public static String getElmo() throws Exception{
        return StringEscapeUtils.escapeHtml(
                new String(Files.readAllBytes(
                        Paths.get(new File("src/main/resources/Example-elmo-Finland.xml").getAbsolutePath()))));
    }

    public static void main(String[] args) {
        SpringApplication.run(FiNcpApplication.class, args);
    }
}
