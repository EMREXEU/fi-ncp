package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.dto.NcpRequestDto;
import fi.csc.emrex.ncp.execption.NpcException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public Finland NCP entry point as specified by EMREX.
 */
@RestController
public class NcpController {

  // TODO: Implement actual EMREX NCP entry point as in spec.
  // EMREX spec: HTTP-POST with sessionId and returnUrl as parameters.
  // After user acceptance: HTTP-POST containing ELMO XML to returnUrl.
  @RequestMapping(value = "/ncp_entry", method = RequestMethod.POST)
  public String handleNcpEntry(@ModelAttribute NcpRequestDto request)
      throws NpcException {
    return "TODO";
  }
}

