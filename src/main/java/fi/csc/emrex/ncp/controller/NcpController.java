package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpSessionAttributes;
import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.exception.NcpException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Public Finland NCP entry point as specified by EMREX.
 */
@Slf4j
@RestController
@CrossOrigin()
public class NcpController {

  @Autowired
  private HttpServletRequest context;

  // EMREX spec: HTTP-POST with sessionId and returnUrl as parameters.
  // After user acceptance: respond with HTTP-POST containing ELMO XML to
  // returnUrl
  // (this entry-point is asynchronous).
  @RequestMapping(value = "/ncp_entry", method = RequestMethod.POST)
  public ModelAndView handleNcpEntry(@ModelAttribute NcpRequestDto request, ModelMap model) throws NcpException {
    HttpSession session = context.getSession();
    session.setAttribute(NcpSessionAttributes.SESSION_ID, request.getSessionId());
    session.setAttribute(NcpSessionAttributes.RETURN_URL, request.getReturnUrl());

    return new ModelAndView("redirect:/ncp", model);
  }
}
