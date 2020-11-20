package fi.csc.emrex.ncp;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import fi.csc.emrex.ncp.controller.NcpSessionAttributes;
import fi.csc.emrex.ncp.elmo.ElmoParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by marko.hollanti on 04/09/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FiNcpApplication.class)
@WebAppConfiguration
@Slf4j
public class ThymeControllerIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void getCourses() throws Exception {

    // First request will store data into session so must use same session in following request
    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .post("/ncp")
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

    ElmoParser elmoParser =
        (ElmoParser) res.getRequest().getSession().getAttribute(NcpSessionAttributes.ELMO);
    log.info("ELMO in session:\n{}", elmoParser.getAllCourseData());

    mockMvc.perform(MockMvcRequestBuilders
        .get("/review")
        .session((MockHttpSession) res.getRequest().getSession())
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk());

  }

}
