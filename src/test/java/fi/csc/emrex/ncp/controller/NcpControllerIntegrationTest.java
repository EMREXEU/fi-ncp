package fi.csc.emrex.ncp.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import fi.csc.emrex.ncp.util.NcpPaths;
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
public class NcpControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * This is the main entry point for NCP.
   */
  @Test
  public void getCourses() throws Exception {

    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .post(NcpPaths.ENTRY_POINT)
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO")
        .sessionAttrs(NcpUiControllerIntegrationTest.getShibbolethAuthenticationAttributes()))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    // TODO: implement actual verification and parameters as specified in (https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf, page 7)
  }
}
