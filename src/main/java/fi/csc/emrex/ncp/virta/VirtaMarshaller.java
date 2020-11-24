package fi.csc.emrex.ncp.virta;

import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 * Created by marko.hollanti on 13/10/15.
 */
@Deprecated
public class VirtaMarshaller {

  public static final String NAMESPACE_URI = "http://purl.org/net/elmo";
  public static final String LOCAL_PART = "elmo";

  // TODO: marshalling XML to response could be one by jackson in controller endpoint?
  public static String marshal(OpintosuorituksetResponse response) throws JAXBException {
    final Marshaller m = JAXBContext.newInstance(OpintosuorituksetResponse.class)
        .createMarshaller();
    m.setProperty(Marshaller.JAXB_FRAGMENT, true);
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    final StringWriter sw = new StringWriter();
    m.marshal(
        new JAXBElement<>(
            new QName(NAMESPACE_URI, LOCAL_PART),
            OpintosuorituksetResponse.class,
            response),
        sw);
    return sw.toString();
  }
}
