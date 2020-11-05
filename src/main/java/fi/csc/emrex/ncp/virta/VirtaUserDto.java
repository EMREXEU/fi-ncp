package fi.csc.emrex.ncp.virta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

/**
 * Created by marko.hollanti on 07/10/15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VirtaUserDto {

  private String oid;
  private String ssn;

  public boolean isOidSet() {
    return !StringUtils.isBlank(oid);
  }
}
