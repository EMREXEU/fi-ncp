package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.NcpTestConstants.SHIBBOLETH_VALUES;
import fi.csc.emrex.ncp.controller.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
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
        .param(SHIBBOLETH_KEYS.LEARNER_ID, SHIBBOLETH_VALUES.LEARNER_ID)
        .param(SHIBBOLETH_KEYS.UNIQUE_ID, SHIBBOLETH_VALUES.UNIQUE_ID)
        .param(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN, SHIBBOLETH_VALUES.ORGANIZATION_DOMAIN)
        .param(SHIBBOLETH_KEYS.ORGANIZATION_ID, SHIBBOLETH_VALUES.ORGANIZATION_ID))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) res.getRequest()
        .getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");
    log.info("VIRTA XML in session:\n{}", XmlUtil.toString(virtaXml));
  }


}
