/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing testing related endpoints.
 *
 * TODO: Disable this controller endpoints by default, enable by configuration.
 */
@RestController
@Slf4j
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TestController extends NcpControllerBase {

  @Autowired
  private HttpServletRequest context;

  /**
   * Mock SSO authentication by copying request parameters to session.
   *
   * After calling this, you can use the real endpoints which expect user data to
   * be stored in session (your client needs to store and use session).
   *
   * @return Attributes stored to session.
   */
  @Deprecated
  @RequestMapping(value = "/mock_shibbolet_auth", method = RequestMethod.GET)
  public Map<String, String> mockShibboleth(@ModelAttribute NcpRequestDto request,
      @RequestParam(SHIBBOLETH_KEYS.UNIQUE_ID) String personId,
      @RequestParam(SHIBBOLETH_KEYS.LEARNER_ID) String learnerId,
      @RequestParam(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN) String schacHomeOrganization,
      @RequestParam(SHIBBOLETH_KEYS.ORGANIZATION_ID) String schacHomeOrganizationId) {

    HttpSession session = context.getSession();
    session.setAttribute(SHIBBOLETH_KEYS.UNIQUE_ID, personId);
    session.setAttribute(SHIBBOLETH_KEYS.LEARNER_ID, learnerId);
    session.setAttribute(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN, schacHomeOrganization);
    session.setAttribute(SHIBBOLETH_KEYS.ORGANIZATION_ID, schacHomeOrganizationId);

    Map<String, String> sessionAttributes = new HashMap<>();
    session.getAttributeNames().asIterator()
        .forEachRemaining(x -> sessionAttributes.put(x, session.getAttribute(x).toString()));
    return sessionAttributes;
  }
}
