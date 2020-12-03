package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.NcpPages;
import fi.csc.emrex.ncp.controller.NcpSessionAttributes;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class TestControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

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
