/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.elmo.XmlUtil;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.service.DataSignService;
import fi.csc.emrex.ncp.service.ElmoService;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

// TODO: Add base path here!
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    //org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
})
@RestController
@Slf4j
public class ThymeController extends NcpControllerBase {

  @Autowired
  private HttpServletRequest context;
  @Autowired
  private VirtaClient virtaClient;
  @Autowired
  private DataSignService dataSignService;
  @Autowired
  private ElmoService elmoService;

  /**
   * TODO: Is this the main entry point for NCP?
   *
   * @param request NCP request as defined for NCP service.
   * @param personId Whole attribute as defined in HAKA Shibboleth.
   * @param learnerId Whole attribute as defined in HAKA Shibboleth.
   * @return View name resolved by $routeProvider in ncp.js. Response data stored in session.
   */
  @RequestMapping(value = "/ncp", method = RequestMethod.POST)
  public String getCourses(
      @ModelAttribute NcpRequestDto request,
      @SessionAttribute("unique-id") String personId,
      @SessionAttribute("SHIB_funetEduPersonLearnerId") String learnerId) throws NpcException {

    HttpSession session = context.getSession();

    log.info("NCP request parameters:{}", request.toString());
    logSession(session);

    if (session.getAttribute(NcpSessionAttributes.SESSION_ID) == null) {
      session.setAttribute(NcpSessionAttributes.SESSION_ID, request.getSessionId());
    }
    if (session.getAttribute(NcpSessionAttributes.RETURN_URL) == null) {
      session.setAttribute(NcpSessionAttributes.RETURN_URL, request.getReturnUrl());
    }

    if (session.getAttribute(NcpSessionAttributes.VIRTA_XML) == null) {
      // Person id needs to be trimmed to match VIRTA.
      // Learner id is used as whole string.
      String[] trimmedPersonIds = personId.split(":");
      String trimmedPersonId = trimmedPersonIds[trimmedPersonIds.length - 1];

      VirtaUserDto virtaUserDto = new VirtaUserDto(learnerId, trimmedPersonId);
      log.info(virtaUserDto.toString());

      OpintosuorituksetResponse virtaXml = virtaClient.fetchStudies(virtaUserDto);
      session.setAttribute(NcpSessionAttributes.VIRTA_XML, virtaXml);
    }

    return NcpPages.NOREX;
  }

  private void logSession(HttpSession session) {
    log.info("Session attributes:");
    session.getAttributeNames().asIterator().forEachRemaining(x ->
        log.info("name:{}, value:{}", x, session.getAttribute(x))
    );
  }

  @RequestMapping(value = "/review", method = RequestMethod.GET)
  public String reviewCourses(
      @RequestParam(value = "courses", required = false) String[] courses,
      Model model) throws NpcException {

    log.info("/review");
    HttpSession session = context.getSession();

    model.addAttribute(
        NcpSessionAttributes.SESSION_ID,
        session.getAttribute(NcpSessionAttributes.SESSION_ID));
    model.addAttribute(
        NcpSessionAttributes.RETURN_URL,
        session.getAttribute(NcpSessionAttributes.RETURN_URL));
    OpintosuorituksetResponse virtaXml =
        (OpintosuorituksetResponse) session.getAttribute(NcpSessionAttributes.VIRTA_XML);

    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      virtaXml = elmoService.trimToSelectedCourses(virtaXml, courseList);
    }

    Elmo elmoXml = elmoService.convertToElmoXml(virtaXml);
    String elmoString = XmlUtil.toString(elmoXml);
    elmoString = dataSignService.sign(elmoString.trim(), StandardCharsets.UTF_8);
    // TODO:  POST ELMO XML to return URL

    // TODO: move this to endpoint after user confirmation?
    elmoService.postElmo(elmoString,
        new NcpRequestDto(
            (String) session.getAttribute(NcpSessionAttributes.SESSION_ID),
            (String) session.getAttribute(NcpSessionAttributes.RETURN_URL)));

    model.addAttribute(NcpSessionAttributes.VIRTA_XML, elmoString);
    model.addAttribute("buttonText", "Confirm selection");
    model.addAttribute("buttonClass", "pure-button custom-go-button custom-inline");
    return NcpPages.REVIEW;
  }

  @RequestMapping(value = "/abort", method = RequestMethod.GET)
  public String abort(Model model) {

    HttpSession session = context.getSession();

    model.addAttribute(
        NcpSessionAttributes.SESSION_ID,
        session.getAttribute(NcpSessionAttributes.SESSION_ID));
    model.addAttribute(
        NcpSessionAttributes.RETURN_URL,
        session.getAttribute(NcpSessionAttributes.RETURN_URL));
    model.addAttribute("buttonText", "Cancel");
    model.addAttribute("buttonClass", "pure-button custom-panic-button custom-inline");
    return NcpPages.REVIEW;
  }

}
