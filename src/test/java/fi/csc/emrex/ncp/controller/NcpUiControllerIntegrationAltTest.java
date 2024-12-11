package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.util.NcpPaths;
import fi.csc.emrex.ncp.util.NcpTestConstants.SHIBBOLETH_VALUES;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@Slf4j
public class NcpUiControllerIntegrationAltTest {
  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  public void setupBeforeEach() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .build();
  }

  @Test
  public void testInvalidIDgetCoursesFail() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders.get(NcpPaths.GET_COURSES).param("sessionId", "TODO").param("returnUrl", "TODO")
                    .requestAttr(SHIBBOLETH_KEYS.UNIQUE_ID, SHIBBOLETH_VALUES.UNIQUE_ID))
            .andDo(print()).andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }
}
