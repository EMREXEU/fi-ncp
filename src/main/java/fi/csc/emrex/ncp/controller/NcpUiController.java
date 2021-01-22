/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpRequestFields.SHIBBOLETH_KEYS;
import fi.csc.emrex.ncp.controller.utils.NcpSessionAttributes;
import fi.csc.emrex.ncp.dto.LearnerDetailsDto;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.service.DataSignService;
import fi.csc.emrex.ncp.service.ElmoService;
import fi.csc.emrex.ncp.util.FidUtil;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotResponse;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Controller providing REST-style endpoints used by fi-ncp fornt-end.
 *
 * NOTE: Actual public EMREX entry point is in NcpController.
 */
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@RestController
@Slf4j
@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true")
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
   * STEP 1: User has logged in to fi-ncp using SSO. Display all user's course data available via
   * fi-ncp to the user. Front-end end provides functionality for selecting courses from this list
   * to STEP 2.
   *
   * @param request NCP request as defined for NCP service.
   * @param personId Whole attribute as defined in HAKA Shibboleth.
   * @param learnerId Whole attribute as defined in HAKA Shibboleth.
   * @return View name resolved by $routeProvider in ncp.js. Response data stored in session.
   */
  @RequestMapping(value = "/courses", method = RequestMethod.GET)
  public OpiskelijanKaikkiTiedotResponse getCourses(
      @RequestHeader Map<String, String> headers,
      @ModelAttribute NcpRequestDto request,
      @SessionAttribute(SHIBBOLETH_KEYS.UNIQUE_ID) String personId,
      @SessionAttribute(SHIBBOLETH_KEYS.LEARNER_ID) String learnerId,
      // TODO: need to verify available shibboleth data related to organization
      @SessionAttribute(SHIBBOLETH_KEYS.ORGANIZATION_DOMAIN) String schacHomeOrganization,
      @SessionAttribute(SHIBBOLETH_KEYS.ORGANIZATION_ID) String schacHomeOrganizationId)
      throws NpcException {

    HttpSession session = context.getSession();

    headers.forEach((key, value) -> {
      log.info(String.format("Header '%s' = %s", key, value));
    });

    log.info("NCP request parameters:{}", request.toString());
    logSession(session);

    // Person id needs to be trimmed to match VIRTA.
    // Learner id is used as whole string.
    String[] trimmedPersonIds = personId.split(":");
    String trimmedPersonId = trimmedPersonIds[trimmedPersonIds.length - 1];

    VirtaUserDto virtaUserDto = new VirtaUserDto(
        learnerId,
        trimmedPersonId,
        schacHomeOrganizationId);

    log.info(virtaUserDto.toString());

    OpiskelijanKaikkiTiedotResponse virtaXml =
        virtaClient.fetchStudiesAndLearnerDetails(virtaUserDto);

    session.setAttribute(NcpSessionAttributes.SESSION_ID, request.getSessionId());
    session.setAttribute(NcpSessionAttributes.RETURN_URL, request.getReturnUrl());
    session.setAttribute(NcpSessionAttributes.VIRTA_XML, virtaXml);
    session.setAttribute(NcpSessionAttributes.VIRTA_USER_DTO, virtaUserDto);

    return virtaXml;
  }

  /**
   * STEP 2: After user has chosen courses from VIRTA XMl in front-end, convert chosen courses to
   * ELMO XML and display generated ELMO XML to user for review. (VIRTA XML has different structure
   * from ELMO XML so user verification for correct conversion is required here)
   */
  @RequestMapping(value = "/review", method = RequestMethod.GET)
  public Elmo reviewCourses(
      @RequestParam(value = "courses", required = false) String[] courses)
      throws NpcException {

    log.info("/review");
    HttpSession session = context.getSession();

    OpiskelijanKaikkiTiedotResponse virtaXml =
        (OpiskelijanKaikkiTiedotResponse) session.getAttribute(NcpSessionAttributes.VIRTA_XML);
    // TODO: student data from shibboleth and VIRTA
    VirtaUserDto student = (VirtaUserDto) session.getAttribute(NcpSessionAttributes.VIRTA_USER_DTO);

    if (virtaXml == null) {
      throw new NpcException("Empty VIRTA XML in session data.");
    }
    if (student == null) {
      throw new NpcException("Empty VIRTA user in session data.");
    }

    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      virtaXml = elmoService.trimToSelectedCourses(virtaXml, courseList);
    }
    OpiskelijanTiedotResponse virtaLearnerDetails = virtaClient.fetchLearnerDetails(student);

    // TODO: read these from VIRTA and/or shibboleth
    LearnerDetailsDto learnerDetails = new LearnerDetailsDto();
    learnerDetails.setBday(FidUtil.resolveBirthDate("", "", virtaXml));
    learnerDetails.setGender(
        new BigInteger(virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukupuoli()));

    Elmo elmoXml = elmoService.convertToElmoXml(virtaXml, student, learnerDetails);
    session.setAttribute(NcpSessionAttributes.ELMO_XML, elmoXml);
    return elmoXml;
  }

  /**
   * STEP 3: After user has reviewed and accepted course data from ELMO XML, post it to return URL.
   *
   * TODO: implement as endpoint for user accepting reviewed courses
   */
  @RequestMapping(value = "/accept", method = RequestMethod.POST)
  public void acceptCourses() throws NpcException {
    HttpSession session = context.getSession();
    Elmo elmoXml = (Elmo) session.getAttribute(NcpSessionAttributes.ELMO_XML);
    if (elmoXml == null) {
      throw new NpcException("Elmo XML is not stored in session.");
    }
    String elmoString = XmlUtil.toString(elmoXml);
    log.info("ELMO XML pre sign:\n{}", elmoString);
    elmoString = dataSignService.sign(elmoString.trim(), StandardCharsets.UTF_8);
    elmoService.postElmo(elmoString,
        new NcpRequestDto(
            (String) session.getAttribute(NcpSessionAttributes.SESSION_ID),
            (String) session.getAttribute(NcpSessionAttributes.RETURN_URL)));
  }

  // TODO: Should user logout and session invalidate also after sending ELMO response to client?
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public ResponseEntity logout() {
    HttpSession session = context.getSession();
    session.invalidate();
    return ResponseEntity.ok().build();
  }

  private void logSession(HttpSession session) {
    log.info("Session attributes:");
    session.getAttributeNames().asIterator().forEachRemaining(x ->
        log.info("name:{}, value:{}", x, session.getAttribute(x))
    );
  }
}
