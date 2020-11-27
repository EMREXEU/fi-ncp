package fi.csc.emrex.ncp;

import fi.csc.emrex.ncp.controller.NcpPages;
import fi.csc.emrex.ncp.controller.NcpSessionAttributes;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FiNcpApplication.class)
@WebAppConfiguration
@Slf4j
public class TestControllerIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeAll
  public void setUp() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void getCoursesTestController() throws Exception {

    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .post("/test/ncp_mock_shibboleth")
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO")
        .param("SHIB_funetEduPersonLearnerId", "1.2.246.562.24.17488477125")
        .param("unique-id", "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpintosuorituksetResponse virtaXml = (OpintosuorituksetResponse) res.getRequest().getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");
    log.info("VIRTA XML in session:\n{}", XmlUtil.toString(virtaXml));
  }


}
