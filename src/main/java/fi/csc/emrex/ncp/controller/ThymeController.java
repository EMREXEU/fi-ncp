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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public String getCourses(@ModelAttribute NcpRequestDto request) throws NpcException {

    log.info("URL: /ncp: " + request.toString());

    if (context.getSession().getAttribute("sessionId") == null) {
      context.getSession().setAttribute("sessionId", request.getSessionId());
    }
    if (context.getSession().getAttribute("returnUrl") == null) {
      context.getSession().setAttribute("returnUrl", request.getReturnUrl());
    }

    if (context.getSession().getAttribute("elmo") == null) {
      // TODO: real ids
      VirtaUserDto virtaUserDto = new VirtaUserDto("17488477125", null);
      String elmoXml = virtaClient.fetchStudies(virtaUserDto);
      ElmoParser parser = new ElmoParser(elmoXml);
      context.getSession().setAttribute("elmo", parser);
    }

    return Pages.NOREX;
  }

  @RequestMapping(value = "/review", method = RequestMethod.GET)
  public String reviewCourses(
      @RequestParam(value = "courses", required = false) String[] courses,
      Model model) throws NpcException {

    log.info("/review");

    model.addAttribute("sessionId", context.getSession().getAttribute("sessionId"));
    model.addAttribute("returnUrl", context.getSession().getAttribute("returnUrl"));
    ElmoParser parser = (ElmoParser) context.getSession().getAttribute("elmo");
    String xmlString;

    if (courses != null && courses.length > 0) {
      List<String> courseList = Arrays.asList(courses);
      xmlString = parser.getCourseData(courseList);
    } else {
      xmlString = parser.getCourseData();
    }

    xmlString = dataSign.sign(xmlString.trim(), StandardCharsets.UTF_8);

    model.addAttribute("elmo", xmlString);
    model.addAttribute("buttonText", "Confirm selection");
    model.addAttribute("buttonClass", "pure-button custom-go-button custom-inline");
    return Pages.REVIEW;
  }

  @RequestMapping(value = "/abort", method = RequestMethod.GET)
  public String abort(Model model) {
    model.addAttribute("sessionId", context.getSession().getAttribute("sessionId"));
    model.addAttribute("returnUrl", context.getSession().getAttribute("returnUrl"));
    model.addAttribute("buttonText", "Cancel");
    model.addAttribute("buttonClass", "pure-button custom-panic-button custom-inline");
    return Pages.REVIEW;
  }

}
