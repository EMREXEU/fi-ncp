package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.exception.NcpException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public Finland NCP entry point as specified by EMREX.
 */
@RestController
@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true")
public class NcpController {

  // TODO: Implement actual EMREX NCP entry point as in spec.
  // TODO: Should this start the server side session in which to store authenticated user details?
  // EMREX spec: HTTP-POST with sessionId and returnUrl as parameters.
  // After user acceptance: respond with  HTTP-POST containing ELMO XML to returnUrl
  // (this entry-point is asynchronous).
  @RequestMapping(value = "/ncp_entry", method = RequestMethod.POST)
  public String handleNcpEntry(@ModelAttribute NcpRequestDto request)
      throws NcpException {
    return "TODO";
  }
}
