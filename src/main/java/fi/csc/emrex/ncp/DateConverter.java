package fi.csc.emrex.ncp;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by marko.hollanti on 05/10/15.
 */
public final class DateConverter {

    private DateConverter() {
        // not to be initialized
    }

    public static XMLGregorianCalendar convertDateToXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
        return date != null ? createXmlGregorianCalendar(date) : null;
    }

    public static String convertXmlGregorianCalendarToString(XMLGregorianCalendar date, String formatStr) {
        Date d = convertXmlGregorianCalendarToDate(date);
        return convertDateToString(d, formatStr);
    }

    public static String convertDateToString(Date date, String formatStr) {
        return new SimpleDateFormat(formatStr).format(date);
    }

    public static Date convertXmlGregorianCalendarToDate(XMLGregorianCalendar date) {
        return date != null ? date.toGregorianCalendar().getTime() : null;
    }

    public static XMLGregorianCalendar convertStringToXmlGregorianCalendar(String dateStr, String formatStr) throws DatatypeConfigurationException, ParseException {
        return dateStr != null && formatStr != null ?
                createXmlGregorianCalendar(new SimpleDateFormat(formatStr).parse(dateStr)) :
                null;
    }

    private static XMLGregorianCalendar createXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }

    public static String convertCurrentDateToString(String formatStr) {
        return convertDateToString(new Date(), formatStr);
    }
}
