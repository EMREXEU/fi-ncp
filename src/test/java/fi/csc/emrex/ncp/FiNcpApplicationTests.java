package fi.csc.emrex.ncp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@ActiveProfiles("dev")
@WebAppConfiguration
public class FiNcpApplicationTests {

  @Test
  public void contextLoads() {
  }
}
