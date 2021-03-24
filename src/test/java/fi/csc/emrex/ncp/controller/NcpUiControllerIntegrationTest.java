package fi.csc.emrex.ncp.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.controller.utils.NcpSessionAttributes;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.util.NcpPaths;
import fi.csc.emrex.ncp.util.NcpTestConstants.SHIBBOLETH_VALUES;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class NcpUiControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getCourses() throws Exception {

    MvcResult res = mockMvc
        .perform(MockMvcRequestBuilders.get(NcpPaths.GET_COURSES).param("sessionId", "TODO").param("returnUrl", "TODO")
            .requestAttr(SHIBBOLETH_KEYS.UNIQUE_ID, SHIBBOLETH_VALUES.UNIQUE_ID))
        .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
        // .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) res.getRequest().getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");
    log.info("VIRTA XML in session:\n{}", XmlUtil.toString(virtaXml));
  }

  @Test
  public void getCoursesAndReviewSelected() throws Exception {

    // First request will store data into session so must use same session in
    // following request
    MvcResult res = mockMvc
        .perform(MockMvcRequestBuilders.get(NcpPaths.GET_COURSES)
            .requestAttr(SHIBBOLETH_KEYS.UNIQUE_ID, SHIBBOLETH_VALUES.UNIQUE_ID))
        .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
        // .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) res.getRequest().getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");

    mockMvc.perform(MockMvcRequestBuilders.get(NcpPaths.REVIEW_COURSES)
        // Expecting this existing course from VIRTA test service
        .param("courses", "1451865").session((MockHttpSession) res.getRequest().getSession())).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  public static Map<String, Object> getShibbolethAuthenticationAttributes() {
    return Map.of(SHIBBOLETH_KEYS.LEARNER_ID, SHIBBOLETH_VALUES.LEARNER_ID, SHIBBOLETH_KEYS.UNIQUE_ID,
        SHIBBOLETH_VALUES.UNIQUE_ID, SHIBBOLETH_KEYS.DATE_OF_BIRTH, SHIBBOLETH_VALUES.DATE_OF_BIRTH,
        SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN, SHIBBOLETH_VALUES.ORGANIZATION_DOMAIN, SHIBBOLETH_KEYS.ORGANIZATION_ID,
        SHIBBOLETH_VALUES.ORGANIZATION_ID, SHIBBOLETH_KEYS.DISPLAY_NAME, SHIBBOLETH_VALUES.DISPLAY_NAME,
        SHIBBOLETH_KEYS.GIVEN_NAME, SHIBBOLETH_VALUES.GIVEN_NAME, SHIBBOLETH_KEYS.SUR_NAME, SHIBBOLETH_VALUES.SUR_NAME);
  }
}
