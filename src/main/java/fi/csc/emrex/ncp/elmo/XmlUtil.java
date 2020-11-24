package fi.csc.emrex.ncp.elmo;

import fi.csc.emrex.ncp.execption.NpcException;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class XmlUtil {

  public static String getStringFromDoc(org.w3c.dom.Document doc) {
    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
    LSSerializer lsSerializer = domImplementation.createLSSerializer();

    LSOutput lsOutput = domImplementation.createLSOutput();
    lsOutput.setEncoding(StandardCharsets.UTF_8.name());
    Writer stringWriter = new StringWriter();
    lsOutput.setCharacterStream(stringWriter);
    lsSerializer.write(doc, lsOutput);
    return stringWriter.toString();
  }

  public static String toString(OpintosuorituksetResponse virtaXml) throws NpcException {
    try {
      // TODO: this writes elements without namespaces, ok?
      StringWriter writer = new StringWriter();
      JAXBContext jc = JAXBContext.newInstance(OpintosuorituksetResponse.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(virtaXml, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new NpcException("Writing VIRTA XML to String failed.", e);
    }
  }

}
