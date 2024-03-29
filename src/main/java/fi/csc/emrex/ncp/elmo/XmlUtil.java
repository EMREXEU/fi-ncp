package fi.csc.emrex.ncp.elmo;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.schemas.elmo.Elmo;
import fi.csc.tietovaranto.luku.OpintosuorituksetResponse;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
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

  public static String toString(OpiskelijanKaikkiTiedotResponse virtaXml) throws NcpException {
    try {
      // TODO: this writes elements without namespaces, ok?
      StringWriter writer = new StringWriter();
      JAXBContext jc = JAXBContext.newInstance(OpiskelijanKaikkiTiedotResponse.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(virtaXml, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new NcpException("Writing VIRTA XML to String failed.", e);
    }
  }

  public static String toString(OpintosuorituksetResponse virtaXml) throws NcpException {
    try {
      // TODO: this writes elements without namespaces, ok?
      StringWriter writer = new StringWriter();
      JAXBContext jc = JAXBContext.newInstance(OpintosuorituksetResponse.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(virtaXml, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new NcpException("Writing VIRTA XML to String failed.", e);
    }
  }


  public static String toString(Elmo elmoXml) throws NcpException {
    try {
      // TODO: this writes elements without namespaces, ok?
      StringWriter writer = new StringWriter();
      // BE CAREFUL! Project has two JAXB modules.
      JAXBContext jc = JAXBContext.newInstance(Elmo.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(elmoXml, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new NcpException("Writing ELMO XML to String failed.", e);
    }
  }

}
