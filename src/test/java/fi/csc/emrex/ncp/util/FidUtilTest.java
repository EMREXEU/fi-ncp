package fi.csc.emrex.ncp.util;

import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FidUtilTest {

  @Test
  public void parseFidFromBday() throws NpcException {
    String shibBday = "19660718";
    String shibUid = "";
    OpintosuorituksetResponse virtaXml = null;
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }

  @Test
  public void parseFidFromUid() throws NpcException {
    String shibBday = "";
    String shibUid = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213";
    OpintosuorituksetResponse virtaXml = null;
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }

  @Test
  public void parseFidFromVirtaXml() throws NpcException {
    String shibBday = "";
    String shibUid = "";
    OpintosuorituksetResponse virtaXml = new OpintosuorituksetResponse();
    XMLGregorianCalendar cal = FidUtil.resolveBirthDate(shibBday, shibUid, virtaXml);

    Assertions.assertEquals(1966, cal.getYear());
    Assertions.assertEquals(7, cal.getMonth());
    Assertions.assertEquals(18, cal.getDay());
  }
}
