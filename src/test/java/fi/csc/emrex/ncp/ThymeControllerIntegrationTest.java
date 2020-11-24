package fi.csc.emrex.ncp;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import fi.csc.emrex.ncp.controller.NcpPages;
import fi.csc.emrex.ncp.controller.NcpSessionAttributes;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.util.Map;
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
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

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

  /**
   * This is the main entry point for NCP.
   */
  @Test
  public void getCourses() throws Exception {

    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .post("/ncp")
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO")
        .sessionAttrs(getShibbolethAuthenticationAttributes()))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpintosuorituksetResponse virtaXml = (OpintosuorituksetResponse) res.getRequest().getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");
    log.info("VIRTA XML in session:\n{}", XmlUtil.toString(virtaXml));
  }

  @Test
  public void getCoursesAndReview() throws Exception {

    // First request will store data into session so must use same session in following request
    MvcResult res = mockMvc.perform(MockMvcRequestBuilders
        .post("/ncp")
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO")
        .sessionAttrs(getShibbolethAuthenticationAttributes()))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(NcpPages.NOREX))
        .andReturn();

    OpintosuorituksetResponse virtaXml = (OpintosuorituksetResponse) res.getRequest().getSession()
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    Assert.notNull(virtaXml, "Elmo session attribute is null");

    mockMvc.perform(MockMvcRequestBuilders
        .get("/review")
        .session((MockHttpSession) res.getRequest().getSession())
        .param("sessionId", "TODO")
        .param("returnUrl", "TODO"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(NcpPages.REVIEW));
  }

  //  Shibboleth session params:
  //SHIB_funetEduPersonLearnerId: 1.2.246.562.24.17488477125
  //SHIB_schacDateOfBirth: 19660718
  //SHIB_schacHomeOrganization: oamk.fi
  //cn: Teppo Testääja
  //displayName: Kaisa
  //givenName: Kaisa
  //sn: Keränen
  //unique-code: urn:mace:terena.org:schac:personalUniqueCode:fi:oamk.fi:x8734
  //unique-id: urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213
  Map<String, Object> getShibbolethAuthenticationAttributes() {
    return Map.of(
        "SHIB_funetEduPersonLearnerId", "1.2.246.562.24.17488477125",
        "SHIB_schacDateOfBirth", "19660718",
        "SHIB_schacHomeOrganization", "oamk.fi",
        "cn", "Teppo Testääja",
        "displayName", "Kaisa",
        "givenName", "Kaisa",
        "sn", "Keränen",
        "unique-code", "urn:mace:terena.org:schac:personalUniqueCode:fi:oamk.fi:x8734",
        "unique-id", "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213");
  }
}
