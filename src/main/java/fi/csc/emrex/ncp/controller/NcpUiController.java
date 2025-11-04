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
import fi.csc.emrex.ncp.dto.NcpResponseDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.emrex.ncp.service.DataSignService;
import fi.csc.emrex.ncp.service.ElmoService;
import fi.csc.emrex.ncp.util.FidUtil;
import fi.csc.emrex.ncp.util.pdfUtil;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import fi.csc.tietovaranto.luku.OpiskelijanTiedotResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.extern.slf4j.Slf4j;
import mace.funet_fi.virta._2015._09._01.OpintosuoritusTyyppi;

import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import static fi.csc.emrex.ncp.util.FidUtil.getFid;
import static fi.csc.emrex.ncp.util.FidUtil.isValid;

/**
 * Controller providing REST-style endpoints used by fi-ncp fornt-end.
 * NOTE: Actual public EMREX entry point is in NcpController.
 */
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class })
@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class NcpUiController extends NcpControllerBase {

  /**
   * Redirect routes that are not recognized in Spring to Angular
   */
  @Bean
  public ErrorViewResolver customErrorViewResolver() {
    final ModelAndView redirectToIndexHtml = new ModelAndView("forward:/index.html", Collections.emptyMap(),
        HttpStatus.OK);
    return (request, status, model) -> status == HttpStatus.NOT_FOUND ? redirectToIndexHtml : null;
  }

  @Autowired
  private HttpServletRequest context;
  @Autowired
  private VirtaClient virtaClient;
  @Autowired
  private DataSignService dataSignService;
  @Autowired
  private ElmoService elmoService;
  @Value("${emrex.fid.validation.enabled:true}")
  private boolean fidValidationEnabled;

  /**
   * @return Map of user related information from Shibboleth/Haka and sessionId &
   *         returnUrl for handling error and cancel cases.
   */
  @RequestMapping(value = "/session", method = RequestMethod.GET)
  public Map<String, String> getSessionInformation() {
    HttpSession session = context.getSession();
    Map<String, String> sessionAttributes = new HashMap<>();
      sessionAttributes.put("givenNames",
          context.getAttribute(SHIBBOLETH_KEYS.GIVEN_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.GIVEN_NAME).toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
              : null);

      sessionAttributes.put("surname",
          context.getAttribute(SHIBBOLETH_KEYS.SUR_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.SUR_NAME).toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
              : null);
      sessionAttributes.put("displayName",
          context.getAttribute(SHIBBOLETH_KEYS.DISPLAY_NAME) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.DISPLAY_NAME).toString().getBytes(StandardCharsets.ISO_8859_1),
                  StandardCharsets.UTF_8)
              : null);
      sessionAttributes.put("commonName",
          context.getAttribute(SHIBBOLETH_KEYS.CN) != null
              ? new String(context.getAttribute(SHIBBOLETH_KEYS.CN).toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
              : "");
      sessionAttributes.put(NcpSessionAttributes.SESSION_ID,
          session.getAttribute(NcpSessionAttributes.SESSION_ID) != null
              ? session.getAttribute(NcpSessionAttributes.SESSION_ID).toString()
              : "");
      sessionAttributes.put(NcpSessionAttributes.RETURN_URL,
          session.getAttribute(NcpSessionAttributes.RETURN_URL) != null
              ? session.getAttribute(NcpSessionAttributes.RETURN_URL).toString()
              : "");

    return sessionAttributes;
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
            : null; // urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213
    String learnerId = context.getAttribute(SHIBBOLETH_KEYS.LEARNER_ID) != null
        ? context.getAttribute(SHIBBOLETH_KEYS.LEARNER_ID).toString()
        : null;

    if (personId == null && learnerId == null) {
      throw new NcpException("Either Unique ID or Learner ID required");
    }

    // Person id needs to be trimmed to match VIRTA.
    // Learner id is used as whole string.
    String fid = "";
    if (personId != null) {
      fid = getFid(personId);

      if (!isValid(fid) && fidValidationEnabled) {
        log.warn("/api/courses Invalid person ID");
        fid = null;  // Filter out trash data from request
      }
    }

    // This could happen when fid is received, but it is invalid, and we do not have learnerid.
    if (fid == null && learnerId == null) {
      throw new NcpException("Validation error: Either Valid Unique ID or Learner ID required");
    }

    // Since VirtaUser can have courses from multiple issuers/orgs, we'll fill that
    // in after the courses are selected in next step.
    VirtaUserDto virtaUserDto = new VirtaUserDto(learnerId, fid, null);

    // Fetch using either fid or learner id.
    OpiskelijanKaikkiTiedotResponse virtaXml = virtaClient.fetchStudiesAndLearnerDetails(virtaUserDto);

    // Try again with learner id, this could happen if we have valid fid according emrex but virta response is empty for some reason.
    if (virtaXml.getVirta().getOpiskelija().isEmpty() && virtaUserDto.isOidSet() && fid != null) {
      log.warn("Got empty response from VIRTA, is fid invalid? Trying with learner id");
      virtaUserDto.setSsn(null);  // remove fid from VIRTA request and use learner id.
      virtaXml = virtaClient.fetchStudiesAndLearnerDetails(virtaUserDto);
    }

    // This should not happen unless something is seriously broken. Probably input fid and/or learner id is invalid.
    if (virtaXml.getVirta().getOpiskelija().isEmpty()) {
      throw new NcpException("Failed to fetch data: got empty response from VIRTA. Check VIRTA data, is fid/learnerid invalid?");
    }

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
  public NcpResponseDto reviewCourses(@RequestParam(value = "courses") String[] courses) throws NcpException, DatatypeConfigurationException {

    HttpSession session = context.getSession();

    OpiskelijanKaikkiTiedotResponse virtaXml = (OpiskelijanKaikkiTiedotResponse) session
        .getAttribute(NcpSessionAttributes.VIRTA_XML);
    VirtaUserDto student = (VirtaUserDto) session.getAttribute(NcpSessionAttributes.VIRTA_USER_DTO);
    if (virtaXml == null) {
      throw new NcpException("Empty VIRTA XML in session data.");
    }
    if (student == null) {
      throw new NcpException("Empty VIRTA user in session data.");
    }

    IssuerDto issuer;
    List<OpintosuoritusTyyppi> allCourses = new ArrayList<>();
    virtaXml.getVirta().getOpiskelija().forEach(HEI -> {
      // Find only relevant data and filter out everything else.
      // Emrex assumes data must have opintosuoritukset-field.
      if (HEI.getOpintosuoritukset() != null) {
        allCourses.addAll(HEI.getOpintosuoritukset().getOpintosuoritus());
      }
    });
    List<OpintosuoritusTyyppi> filteredCourses;
    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      OpintosuoritusTyyppi opintosuoritusTyyppi =
              allCourses.stream().filter(
                      c -> c.getAvain().equals(courseList.get(0))).findFirst()
          .orElse(null);
      if (opintosuoritusTyyppi == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No course match found for courseList[0]");
      }

      issuer = elmoService.issuerForCode(opintosuoritusTyyppi.getMyontaja());
      filteredCourses = elmoService.trimToSelectedCourses(allCourses, courseList);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No courses selected");
    }

    List<OpintosuoritusTyyppi> allCoursesFromSelectedIssuer =
            allCourses.stream().filter(
                    c -> c.getMyontaja().equals(issuer.getCode()))
                    .collect(Collectors.toList());

    student.setOrg(issuer.getCode());
    OpiskelijanTiedotResponse virtaLearnerDetails = virtaClient.fetchLearnerDetails(student);
    if (virtaLearnerDetails.getOpiskelijat().getOpiskelija().isEmpty()) {
      log.error("Opiskelija not found: got empty response");
      throw new NcpException("Student not found.");
    }

    // Default to ISO/IEC 5218 0 = Not known if virta data doesn't have gender
    String gender = virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukupuoli() != null
        ? virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukupuoli()
        : "0";

    String schacBday = context.getAttribute(SHIBBOLETH_KEYS.DATE_OF_BIRTH) != null
        ? context.getAttribute(SHIBBOLETH_KEYS.DATE_OF_BIRTH).toString()
        : "";
    String personId = context.getAttribute(SHIBBOLETH_KEYS.UNIQUE_ID) != null
        ? context.getAttribute(SHIBBOLETH_KEYS.UNIQUE_ID).toString()
        : "";

    if (personId != null) {
      if (!isValid(getFid(personId)) && fidValidationEnabled) {
        log.warn("/api/review Invalid person ID");
        personId = null;
      }
    }

    LearnerDetailsDto learnerDetails = new LearnerDetailsDto();
    // Optional bday element, see Elmo schema for more info
    try {
      learnerDetails.setBday(FidUtil.resolveBirthDate(schacBday, personId, virtaXml));
    } catch (IndexOutOfBoundsException|NcpException|NumberFormatException e) {
        log.warn("Birthday resolve failed: {}", e.getMessage());
    }
    learnerDetails.setGender(new BigInteger(gender));
    // Emrex must provide empty string if etunimet is null according elmo spec
    learnerDetails.setGivenNames(Optional.ofNullable(virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getEtunimet()).orElse(""));
    learnerDetails.setFamilyName(Optional.ofNullable(virtaLearnerDetails.getOpiskelijat().getOpiskelija().get(0).getSukunimi()).orElse(""));

    String alternativeName = String.format("%s %s", learnerDetails.getGivenNames(), learnerDetails.getFamilyName());
    learnerDetails.setAlternateName(alternativeName);

    Elmo elmoXml = elmoService.convertToElmoXml(filteredCourses, allCoursesFromSelectedIssuer, student, learnerDetails);
    String PDFDataURI = "";

    try {
      PDFDataURI = pdfUtil.convertToPDFdataURI(XmlUtil.toString(elmoXml));
    } catch (FOPException | IOException | TransformerException e) {
      log.error("convertToPDFdataURI failed", e);
    }

    if (!PDFDataURI.isEmpty()) {
      elmoService.addAttachment(elmoXml, PDFDataURI);
    }

    String elmoString = XmlUtil.toString(elmoXml);
    // log.info("elmoString: {}", elmoString);

    elmoString = dataSignService.sign(elmoString.trim(), StandardCharsets.UTF_8);
    // log.info("signed, gzipped, base64encoded: {}", elmoString);

    /*
     * log.info("Validate signature"); try { String s =
     * dataSignService.decodeAndDecompress(elmoString); Boolean valid =
     * dataSignService.verifySignature(s); log.info("XML Signature valid: {}",
     * valid); } catch (Exception e) { e.printStackTrace(); }
     */

    String sessionId = session.getAttribute(NcpSessionAttributes.SESSION_ID) != null
        ? session.getAttribute(NcpSessionAttributes.SESSION_ID).toString()
        : "";
    String returnUrl = session.getAttribute(NcpSessionAttributes.RETURN_URL) != null
        ? session.getAttribute(NcpSessionAttributes.RETURN_URL).toString()
        : "";

    NcpResponseDto ncpResponseDto = new NcpResponseDto();
    ncpResponseDto.setSessionId(sessionId);
    ncpResponseDto.setReturnCode("NCP_OK");
    ncpResponseDto.setReturnMessage("");
    ncpResponseDto.setReturnUrl(returnUrl);
    ncpResponseDto.setElmo(elmoString);
    ncpResponseDto.setElmoXml(elmoXml);

    return ncpResponseDto;
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public ResponseEntity logout(HttpServletResponse response) {
    HttpSession session = context.getSession();
    session.invalidate();
    return ResponseEntity.ok().build();
  }
}
