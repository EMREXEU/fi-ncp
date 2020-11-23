/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.elmo.ElmoParser;
import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.emrex.ncp.service.DataSign;
import fi.csc.emrex.ncp.virta.VirtaClient;
import fi.csc.emrex.ncp.virta.VirtaUserDto;
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

/**
 * @author salum
 */
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
  private DataSign dataSign;

  /**
   * TODO: Is this the main entry point for NCP?
   *
   * @param request NCP request as defined for NCP service.
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

    if (session.getAttribute(NcpSessionAttributes.ELMO) == null) {
      // TODO: real ids
      //String[] trimmedLearnerIds = learnerId.split(".");// TODO: not needed?
      String[] trimmedPersonIds = personId.split(":");
      //String trimmedLearnerId = trimmedLearnerIds[trimmedLearnerIds.length - 1];
      String trimmedPersonId = trimmedPersonIds[trimmedPersonIds.length - 1];

      VirtaUserDto virtaUserDto = new VirtaUserDto(learnerId, trimmedPersonId);
      log.info(virtaUserDto.toString());

      String elmoXml = virtaClient.fetchStudies(virtaUserDto);
      ElmoParser parser = new ElmoParser(elmoXml);
      session.setAttribute(NcpSessionAttributes.ELMO, parser);

      // TODO: remove
      log.info("VIRTA XML:\n{}", elmoXml);
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
    ElmoParser parser = (ElmoParser) session.getAttribute(NcpSessionAttributes.ELMO);
    String xmlString;

    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      xmlString = parser.getCourseData(courseList);
    } else {
      xmlString = parser.getAllCourseData();
    }

    xmlString = dataSign.sign(xmlString.trim(), StandardCharsets.UTF_8);

    model.addAttribute(NcpSessionAttributes.ELMO, xmlString);
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
