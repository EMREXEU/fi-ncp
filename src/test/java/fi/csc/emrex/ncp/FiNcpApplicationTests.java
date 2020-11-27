package fi.csc.emrex.ncp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
//@SpringApplicationConfiguration(classes = FiNcpApplication.class)
@SpringBootTest
@WebAppConfiguration
@Disabled
public class FiNcpApplicationTests {

  @Test
  public void contextLoads() {
  }

}
