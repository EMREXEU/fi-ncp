/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.controller.utils.NcpSessionAttributes;
import fi.csc.emrex.ncp.dto.IssuerDto;
import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.emrex.ncp.service.DataSignService;
import fi.csc.emrex.ncp.service.ElmoService;
import fi.csc.emrex.ncp.util.FidUtil;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller providing REST-style endpoints used by fi-ncp fornt-end.
 *
 * NOTE: Actual public EMREX entry point is in NcpController.
 */
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class NcpUiController extends NcpControllerBase {

  @Autowired
  private HttpServletRequest context;
  @Autowired
  private VirtaClient virtaClient;
  @Autowired
  private DataSignService dataSignService;
  @Autowired
  private ElmoService elmoService;

  /**
   * @return Map of user related information from Shibboleth/Haka.
   */
  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public Map<String, String> getUserInformation() {
    Map<String, String> userAttributes = new HashMap<>();
    try {
      userAttributes.put("givenNames",
          context.getAttribute(SHIBBOLETH_KEYS.GIVEN_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.GIVEN_NAME).toString().getBytes("ISO-8859-1"), "UTF-8")
              : null);

      userAttributes.put("surname",
          context.getAttribute(SHIBBOLETH_KEYS.SUR_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.SUR_NAME).toString().getBytes("ISO-8859-1"), "UTF-8")
              : null);
      userAttributes.put("displayName",
          context.getAttribute(SHIBBOLETH_KEYS.DISPLAY_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.DISPLAY_NAME).toString().getBytes("ISO-8859-1"),
                  "UTF-8")
              : null);
      userAttributes.put("commonName",
          context.getAttribute(SHIBBOLETH_KEYS.CN) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.CN).toString().getBytes("ISO-8859-1"), "UTF-8")
              : null);
    } catch (UnsupportedEncodingException e1) {
    }
    if (context.getAttribute(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN) != null) {
      try {
        IssuerDto issuer = (IssuerDto) elmoService
            .issuerForDomain(context.getAttribute(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN).toString());
        userAttributes.put("homeOrganization", issuer.getTitle());
        userAttributes.put("homeOrganizationId", issuer.getCode());
      } catch (NcpException e) {
      }
    }
    // log.info("userAttributes:{}", userAttributes);

    return userAttributes;
  }

  /**
   * @return Map of issuers.
   */
  @RequestMapping(value = "/issuers", method = RequestMethod.GET)
  public Map<String, IssuerDto> getIssuers() {
    return elmoService.getVirtaIssuerCodeToIssuer();
  }

  /**
   * STEP 1: User has logged in to fi-ncp using SSO. Display all user's course
   * data available via fi-ncp to the user. Front-end end provides functionality
   * for selecting courses from this list to STEP 2.
   *
   * @param request NCP request as defined for NCP service.
   * @return View name resolved by $routeProvider in ncp.js. Response data stored
   *         in session.
   */
  @RequestMapping(value = "/courses", method = RequestMethod.GET)
  public OpiskelijanKaikkiTiedotResponse getCourses(@ModelAttribute NcpRequestDto request) throws NcpException {

    HttpSession session = context.getSession();

    String personId = context.getAttribute(SHIBBOLETH_KEYS.UNIQUE_ID) != null
        ? context.getAttribute(SHIBBOLETH_KEYS.UNIQUE_ID).toString()
        : null;
    String learnerId = context.getAttribute(SHIBBOLETH_KEYS.LEARNER_ID) != null
        ? context.getAttribute(SHIBBOLETH_KEYS.LEARNER_ID).toString()
        : null;

    if (personId == null && learnerId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either Unique ID or Learner ID required");
    }

    // Person id needs to be trimmed to match VIRTA.
    // Learner id is used as whole string.
    String[] trimmedPersonIds = personId.split(":");
    String trimmedPersonId = trimmedPersonIds[trimmedPersonIds.length - 1];

    // Since VirtaUser can have courses from multiple issuers/orgs, we'll fill that
    // in after the courses are selected in next step
    VirtaUserDto virtaUserDto = new VirtaUserDto(learnerId, trimmedPersonId, null);

    OpiskelijanKaikkiTiedotResponse virtaXml = virtaClient.fetchStudiesAndLearnerDetails(virtaUserDto);

    session.setAttribute(NcpSessionAttributes.SESSION_ID, request.getSessionId());
    session.setAttribute(NcpSessionAttributes.RETURN_URL, request.getReturnUrl());
    session.setAttribute(NcpSessionAttributes.VIRTA_XML, virtaXml);
    session.setAttribute(NcpSessionAttributes.VIRTA_USER_DTO, virtaUserDto);

    return virtaXml;
  }

  /**
   * STEP 2: After user has chosen courses from VIRTA XMl in front-end, convert
   * chosen courses to ELMO XML and display generated ELMO XML to user for review.
   * (VIRTA XML has different structure from ELMO XML so user verification for
   * correct conversion is required here)
   */
  @RequestMapping(value = "/review", method = RequestMethod.GET)
  public Elmo reviewCourses(@RequestParam(value = "courses") String[] courses) throws NcpException {

    HttpSession session = context.getSession();

    OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) session
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    // TODO: student data from shibboleth and VIRTA
    VirtaUserDto student = (VirtaUserDto) session.getAttribute(NcpSessionAttributes.VIRTA_USER_DTO);
    if (virtaXml == null) {
      throw new NcpException("Empty VIRTA XML in session data.");
    }
    if (student == null) {
      throw new NcpException("Empty VIRTA user in session data.");
    }
    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      virtaXml = elmoService.trimToSelectedCourses(virtaXml, courseList);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No courses selected");
    }
    // Set issuer of the first course as student.org (at this point there are only
    // courses from a single issuer/org)
    student.setOrg(
        virtaXml.getVirta().getOpiskelija().get(0).getOpintosuoritukset().getOpintosuoritus().get(0).getMyontaja());
    OpiskelijanTiedotResponse virtaLearnerDetails = virtaClient.fetchLearnerDetails(student);

    // Default to ISO/IEC 5218 0 = Not known if virta data doesn't have gender
    String gender = virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukupuoli() != null
        ? virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukupuoli()
        : "0";

    // TODO: read these from VIRTA and/or shibboleth
    LearnerDetailsDto learnerDetails = new LearnerDetailsDto();
    learnerDetails.setBday(FidUtil.resolveBirthDate("", "", virtaXml));
    learnerDetails.setGender(new BigInteger(gender));

    Elmo elmoXml = elmoService.convertToElmoXml(virtaXml, student, learnerDetails);
    session.setAttribute(NcpSessionAttributes.ELMO_XML, elmoXml);
    return elmoXml;
  }

  /**
   * STEP 3: After user has reviewed and accepted course data from ELMO XML, post
   * it to return URL.
   *
   * TODO: implement as endpoint for user accepting reviewed courses
   */
  @RequestMapping(value = "/accept", method = RequestMethod.POST)
  public void acceptCourses() throws NcpException {
    HttpSession session = context.getSession();
    Elmo elmoXml = (Elmo) session.getAttribute(NcpSessionAttributes.ELMO_XML);
    if (elmoXml == null) {
      throw new NcpException("Elmo XML is not stored in session.");
    }
    String elmoString = XmlUtil.toString(elmoXml);
    log.info("ELMO XML pre sign:\n{}", elmoString);
    elmoString = dataSignService.sign(elmoString.trim(), StandardCharsets.UTF_8);
    elmoService.postElmo(elmoString, new NcpRequestDto((String) session.getAttribute(NcpSessionAttributes.SESSION_ID),
        (String) session.getAttribute(NcpSessionAttributes.RETURN_URL)));
  }

  // TODO: Should user logout and session invalidate also after sending ELMO
  // response to client?
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public ResponseEntity logout() {
    HttpSession session = context.getSession();
    session.invalidate();
    return ResponseEntity.ok().build();
  }

  private void logSession(HttpSession session) {
    log.info("Session attributes:");
    session.getAttributeNames().asIterator()
        .forEachRemaining(x -> log.info("name:{}, value:{}", x, session.getAttribute(x)));
  }
}
