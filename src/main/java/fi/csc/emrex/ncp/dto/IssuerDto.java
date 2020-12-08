package fi.csc.emrex.ncp.dto;

import fi.csc.schemas.elmo.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 *           <issuer>
 *             <country>FI</country>
 *             <identifier type="TODO:type">oamk.fi</identifier>
 *             <title xml:lang="FI">TODO: Oikea myöntäjän nimien konfiguraatio</title>
 *             <url>TODO</url>
 *         </issuer>
 * </pre>
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IssuerDto {

  private CountryCode countryCode;
  private String identifierType;
  private String identifier;
  private String title;
  private String url;
}
