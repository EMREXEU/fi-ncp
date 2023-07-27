package fi.csc.emrex.ncp.controller;

import fi.csc.emrex.ncp.exception.NcpException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class NcpControllerBase {
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NcpException.class)
  public ResponseEntity handleException(HttpServletRequest req, NcpException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity handleRuntimeException(HttpServletRequest req, RuntimeException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
  }
}
