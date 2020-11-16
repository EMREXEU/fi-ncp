package fi.csc.emrex.ncp.elmo;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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

}
