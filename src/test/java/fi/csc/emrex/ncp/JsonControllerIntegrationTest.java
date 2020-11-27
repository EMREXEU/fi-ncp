package fi.csc.emrex.ncp;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by marko.hollanti on 04/09/15.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FiNcpApplication.class)
@WebAppConfiguration
public class JsonControllerIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeAll
  public void setUp() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void testFetchElmoXml() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders
        .get("/elmo"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
    //    .andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
    ;
  }
}
