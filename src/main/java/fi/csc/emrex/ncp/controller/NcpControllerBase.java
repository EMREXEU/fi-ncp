package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.execption.NpcException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class NcpControllerBase {


  // TODO: move to base class if required
  // TODO: return error in format required by NPC specification
  @ExceptionHandler(NpcException.class)
  public String handleException(HttpServletRequest req, NpcException e) {
    // TODO: return proper HTTP code and message.
    log.error(e.getMessage(), e);
    return e.getMessage();
  }

  // TODO: move to base class if required
  // TODO: return error in format required by NPC specification
  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(HttpServletRequest req, RuntimeException e) {
    log.error(e.getMessage(), e);
    return e.getMessage();
  }
}
