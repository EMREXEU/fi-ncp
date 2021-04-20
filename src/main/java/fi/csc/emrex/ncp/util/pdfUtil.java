package fi.csc.emrex.ncp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class pdfUtil {
    public static final String RESOURCES_DIR;
    //public static final String OUTPUT_DIR;

    static {
        RESOURCES_DIR = "src//main//resources//";
        //OUTPUT_DIR = "src//main//resources//output//";
    }

    /**
   * @return PDF data URI as string
   */
    public static String convertToPDFdataURI(String xmlString) throws IOException, FOPException, TransformerException {
        xmlString = xmlString.replaceAll("<elmo.*>", "<elmo>");
        // log.info("{}", xmlString);
        // the XSL FO file
        File xsltFile = new File(RESOURCES_DIR + "//elmo//xml-fo-template-simple.xsl");
        // the XML file which provides the input
        StreamSource xmlSource = new StreamSource(new StringReader(xmlString));
        // create an instance of fop factory
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        // a user agent is needed for transformation
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        // Setup output
        // OutputStream out;
        // out = new java.io.FileOutputStream(OUTPUT_DIR + "//output.pdf");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            // Resulting SAX events (the generated FO) must be piped through to
            // FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            // That's where the XML is first transformed to XSL-FO and then
            // PDF is created
            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("data:image/png;base64,");
        sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(out.toByteArray(), false)));
        //log.info("{}", sb.toString());

        return sb.toString();
    }
}
