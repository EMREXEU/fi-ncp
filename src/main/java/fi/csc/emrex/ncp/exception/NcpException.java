package fi.csc.emrex.ncp.exception;

public class NcpException extends Exception {

  public NcpException() {
    super();
  }

  public NcpException(String message) {
    super(message);
  }

  public NcpException(String message, Throwable cause) {
    super(message, cause);
  }

  public NcpException(Throwable cause) {
    super(cause);
  }

}
