package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.controller.utils.NcpReturnCodes;
import fi.csc.emrex.ncp.execption.NpcException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO: check return requirements
// https://confluence.csc.fi/display/EMREX/Implementation+details%3A+NCP
// https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf, page 7
@Slf4j
public class NcpControllerBase {

  // TODO: return error in format required by NPC specification
  // TODO: return proper HTTP code and message.
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NpcException.class)
  public String handleException(HttpServletRequest req, NpcException e) {
    log.error(e.getMessage(), e);
    return String.format("%s: %s", NcpReturnCodes.NCP_ERROR, e.getMessage());
  }

  // TODO: return error in format required by NPC specification
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(HttpServletRequest req, RuntimeException e) {
    log.error(e.getMessage(), e);
    return String.format("%s: %s", NcpReturnCodes.NCP_ERROR, e.getMessage());
  }
}
