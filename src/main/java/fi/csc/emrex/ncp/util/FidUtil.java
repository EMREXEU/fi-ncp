package fi.csc.emrex.ncp.util;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Utility for resolving Finnish Personal ID.
 */
public class FidUtil {
  private static final BigDecimal DIVISOR = new BigDecimal(31);
  private static final String CONTROL_CHARS = "0123456789ABCDEFHJKLMNPRSTUVWXY";

  /**
   * <pre>
   *     SHIB_schacDateOfBirth: 19660718
   *     unique-id: urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213
   *
   * </pre>
   *
   * @param shibBday primary source (19660718)
   * @param shibUid secondary source (urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213)
   * @param virtaXml tertiary source (VIRTA XML)
   * @return Date of birth parsed from one of the sources in precedence order.
   */
  public static XMLGregorianCalendar resolveBirthDate(
      String shibBday,
      String shibUid,
      OpiskelijanKaikkiTiedotResponse virtaXml) throws NcpException {
    try {
      int day;
      int month;
      int year;
      if (shibBday != null && !shibBday.isEmpty()) {
        // 19660718
        day = Integer.parseInt(shibBday.substring(6, 8));
        month = Integer.parseInt(shibBday.substring(4, 6));
        year = Integer.parseInt(shibBday.substring(0, 4));
      } else if (shibUid != null && !shibUid.isEmpty()) {
        String fid = getFid(shibUid);
        day = Integer.parseInt(fid.substring(0, 2));
        month = Integer.parseInt(fid.substring(2, 4));
        year = resolveYearFromFid(fid);
      } else if (virtaXml != null) {
        // OpiskelijanKaikkiTiedotResponse.Virta.Opiskelija.Henkilotunnus
        String fid = virtaXml.getVirta().getOpiskelija().get(0).getHenkilotunnus();
        if (fid == null || fid.isEmpty()) {
          throw new NcpException("henkilotunnus is null or empty");  // possible foreign student without hetu or virtaxml incomplete
        }
        day = Integer.parseInt(fid.substring(0, 2));
        month = Integer.parseInt(fid.substring(2, 4));
        year = resolveYearFromFid(fid);
      } else {
        throw new NcpException("Parsing birth date failed: no source date available.");
      }

      return DatatypeFactory.newInstance()
          .newXMLGregorianCalendarDate(year, month, day, DatatypeConstants.FIELD_UNDEFINED);
    } catch (DatatypeConfigurationException e) {
      throw new NcpException("Parsing birth date failed.", e);
    }
  }

  /**
   * @param fid Finnish Personal ID: PPKKVVXNNNT -> VVVV
   */
  private static int resolveYearFromFid(String fid) throws NcpException {

    char centuryChar = fid.charAt(6);
    String yearPreStr;
    String yearPostStr = fid.substring(4, 6);
      if (centuryChar == '+') {
          yearPreStr = "18";
      } else if (
              centuryChar == '-' ||
              centuryChar == 'Y' ||
              centuryChar == 'X' ||
              centuryChar == 'W' ||
              centuryChar == 'V' ||
              centuryChar == 'U') {
          yearPreStr = "19";
      } else if (
              centuryChar == 'A' ||
              centuryChar == 'B' ||
              centuryChar == 'C' ||
              centuryChar == 'D' ||
              centuryChar == 'E' ||
              centuryChar == 'F') {
          yearPreStr = "20";
      } else {
          throw new NcpException(
                  "Parsing birth date failed: cannot resolve century from fid character:" + centuryChar);
      }
    return Integer.parseInt(yearPreStr + yearPostStr);
  }

  /**
   * Calculate and return control character for input(bday + invididual number)
   * When fid is 131052-308T then input should be 131052308.
   * Divide input by 31 and then multiply reminder by 31 and round towards "nearest neighbor".
   * Complete guide and examples.
   * <a href="https://dvv.fi/en/personal-identity-code2">Personal identity code</a>
   * <a href="https://finlex.fi/fi/laki/ajantasa/2010/20100128">Personal identity code law</a>
   * @param input birthday and invididual number combined as one integer.
   * @return Control character
   */
  public static char calculateControlCharacter(int input) {
    BigDecimal decimalPart = new BigDecimal(input).divide(DIVISOR, 25, RoundingMode.HALF_UP).remainder(BigDecimal.ONE);
    BigDecimal decimalPartMul = decimalPart.multiply(DIVISOR);
    int controlCharIndex = decimalPartMul.setScale(0, RoundingMode.HALF_UP).intValue();
    return CONTROL_CHARS.charAt(controlCharIndex);
  }

  /**
   *
   * @param fid - finnish personal identification
   * @return true for valid fid else false
   */
  public static boolean isValid(String fid) {
    if (fid == null || fid.isEmpty()) {
      return false;
    }
    // Validate length
    if (fid.length() != 11) {
      return false;
    }
    // validate bday
    if (!validateBday(fid)) {
      return false;
    }
    // Validate control char
    try {
      return fid.charAt(10) == calculateControlCharacter(Integer.parseInt(fid.substring(0, 6) + fid.substring(7, 10)));
    } catch (NumberFormatException|ArithmeticException|IndexOutOfBoundsException e) {
      return false;
    }
  }

  private static boolean validateBday(String fid) {
    if (fid == null || fid.isEmpty()) {
      return false;
    }
    int day, month;
    // Should be valid int
    try {
      day = Integer.parseInt(fid.substring(0, 2));
      month = Integer.parseInt(fid.substring(2, 4));
      Integer.parseInt(fid.substring(4, 6));
    } catch (NumberFormatException e) {
      return false;
    }
    int yyyy;
    // Should be valid year
      try {
          yyyy = resolveYearFromFid(fid);
      } catch (NcpException|IndexOutOfBoundsException|NumberFormatException e) {
          return false;
      }
      // Should be valid date
      try {
        LocalDate.of(yyyy, month, day);
      } catch (DateTimeException e) {
        return false;
      }
      return true;
  }

  public static String getFid(String shibUid) {
    if (shibUid == null || shibUid.isEmpty()) {
      return null;
    }
    try {
      // unique-id: urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213
      String[] shibParts = shibUid.split(":");
      return shibParts[shibParts.length - 1];
    } catch (Exception e) {
      return null;
    }
  }
}
