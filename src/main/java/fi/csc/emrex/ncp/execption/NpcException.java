package fi.csc.emrex.ncp.execption;

public class NpcException extends Exception {

  public NpcException() {
    super();
  }

  public NpcException(String message) {
    super(message);
  }

  public NpcException(String message, Throwable cause) {
    super(message, cause);
  }

  public NpcException(Throwable cause) {
    super(cause);
  }

}
