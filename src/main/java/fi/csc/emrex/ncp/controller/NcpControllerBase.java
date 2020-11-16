package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.execption.NpcException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

// TODO: check return requirements
// https://confluence.csc.fi/display/EMREX/Implementation+details%3A+NCP
@Slf4j
public class NcpControllerBase {

  // TODO: return error in format required by NPC specification
  @ExceptionHandler(NpcException.class)
  public String handleException(HttpServletRequest req, NpcException e) {
    // TODO: return proper HTTP code and message.
    log.error(e.getMessage(), e);
    return String.format("%s: %s", NcpReturnCodes.NCP_ERROR, e.getMessage());
  }

  // TODO: return error in format required by NPC specification
  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(HttpServletRequest req, RuntimeException e) {
    log.error(e.getMessage(), e);
    return String.format("%s: %s", NcpReturnCodes.NCP_ERROR, e.getMessage());
  }
}
