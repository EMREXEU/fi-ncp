package fi.csc.emrex.ncp.dto;

import fi.csc.emrex.ncp.service.ElmoService.ISSUER_FILE_COLUMN;
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

  private final static String IDENTIFIER_TYPE = "SCHAC";
  private static final String DOMAIN_PREFIX = "https://";

  private String code;
  private CountryCode countryCode;
  private String identifierType;
  private String identifier;
  private String title;
  private String domain;
  private String url;

  /**
   * Korkeakoulu;TK-oppilaitoskoodi;Domain = schac
   *
   * @param args Values read from config file line
   */
  public IssuerDto(String[] args) {
    code = args[ISSUER_FILE_COLUMN.CODE.ordinal()];
    countryCode = CountryCode.FI;
    identifierType = IDENTIFIER_TYPE;
    identifier = args[ISSUER_FILE_COLUMN.DOMAIN.ordinal()];
    title = args[ISSUER_FILE_COLUMN.TITLE.ordinal()];
    domain = args[ISSUER_FILE_COLUMN.DOMAIN.ordinal()];
    url = DOMAIN_PREFIX + args[ISSUER_FILE_COLUMN.DOMAIN.ordinal()];
  }
}
