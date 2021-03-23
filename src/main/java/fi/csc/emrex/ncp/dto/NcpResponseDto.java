package fi.csc.emrex.ncp.dto;

import fi.csc.schemas.elmo.Elmo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by jpentika on 19/08/15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NcpResponseDto {
  private String sessionId;
  private String returnUrl;
  private String returnCode;
  private String returnMessage;
  private String elmo;
  private Elmo elmoXml;
}
