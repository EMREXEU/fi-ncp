package fi.csc.emrex.ncp.virta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

/**
 * VIRTA user. Identified either by oid or ssn;
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VirtaUserDto {

  private String oid;
  private String ssn;

  public boolean isSsnSet() {
    return !StringUtils.isBlank(ssn);
  }
}
