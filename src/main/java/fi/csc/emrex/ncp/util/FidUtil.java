package fi.csc.emrex.ncp.util;

import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Utility for resolving Finnish Personal ID.
 */
public class FidUtil {

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
      OpiskelijanKaikkiTiedotResponse virtaXml) throws NpcException {
    try {
      // TODO
      int day;
      int month;
      int year;
      if (shibBday != null && !shibBday.isEmpty()) {
        // 19660718
        day = Integer.parseInt(shibBday.substring(6, 8));
        month = Integer.parseInt(shibBday.substring(4, 6));
        year = Integer.parseInt(shibBday.substring(0, 4));
      } else if (shibUid != null && !shibUid.isEmpty()) {
        // unique-id: urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213
        String[] x = shibUid.split(":");
        String fid = x[x.length - 1];
        day = Integer.parseInt(fid.substring(0, 2));
        month = Integer.parseInt(fid.substring(2, 4));
        year = resolveYearFromFid(fid);
      } else if (virtaXml != null) {
        // TODO: OpiskelijanKaikkiTiedotResponse.Virta.Opiskelija.Henkilotunnus
        String fid = virtaXml.getVirta().getOpiskelija().get(0).getHenkilotunnus();
        day = Integer.parseInt(fid.substring(0, 2));
        month = Integer.parseInt(fid.substring(2, 4));
        year = resolveYearFromFid(fid);
      } else {
        throw new NpcException("Parsing birth date failed: no source date available.");
      }

      return DatatypeFactory.newInstance()
          .newXMLGregorianCalendarDate(year, month, day, DatatypeConstants.FIELD_UNDEFINED);
    } catch (DatatypeConfigurationException e) {
      throw new NpcException("Parsing birth date failed.", e);
    }
  }

  /**
   * @param fid Finnish Personal ID: PPKKVVXNNNT -> VVVV
   */
  private static int resolveYearFromFid(String fid) throws NpcException {

    char centuryChar = fid.charAt(6);
    String yearPreStr;
    String yearPostStr = fid.substring(4, 6);
    switch (centuryChar) {
      case '+':
        yearPreStr = "18";
        break;
      case '-':
        yearPreStr = "19";
        break;
      case 'A':
        yearPreStr = "20";
        break;
      default:
        throw new NpcException(
            "Parsing birth date failed: cannot resolve century from fid characer:" + centuryChar);
    }
    return Integer.parseInt(yearPreStr + yearPostStr);
  }

}
