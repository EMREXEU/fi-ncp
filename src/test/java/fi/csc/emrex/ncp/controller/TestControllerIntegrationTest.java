package fi.csc.emrex.ncp.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import fi.csc.emrex.ncp.NcpTestConstants.SHIBBOLETH_VALUES;
import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class TestControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void mockLogin() throws Exception {

    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .get(NcpPaths.MOCK_SHIBBOLETH_AUTH)
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO")
        .param(SHIBBOLETH_KEYS.LEARNER_ID, SHIBBOLETH_VALUES.LEARNER_ID)
        .param(SHIBBOLETH_KEYS.UNIQUE_ID, SHIBBOLETH_VALUES.UNIQUE_ID)
        .param(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN, SHIBBOLETH_VALUES.ORGANIZATION_DOMAIN)
        .param(SHIBBOLETH_KEYS.ORGANIZATION_ID, SHIBBOLETH_VALUES.ORGANIZATION_ID))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
  }
}
