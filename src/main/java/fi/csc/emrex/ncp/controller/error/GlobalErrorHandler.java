package fi.csc.emrex.ncp.controller.error;

import fi.csc.emrex.ncp.controller.error.security.EncryptUtil;
import fi.csc.tietovaranto.luku.OpiskelijanKaikkiTiedotResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import mace.funet_fi.virta._2015._09._01.Virta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.security.PublicKey;
import java.util.UUID;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

@Service
public class GlobalErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class.getName());

    @Value("classpath:virta/anonymize.xsl")
    private Resource anonymizeStylesheet;

    @Value("${emrex.logging.secure.public-key.path}")
    private String publicKeyPath;

    @Value("${emrex.logging.secure.file.path}")
    private String securePath;

    public void handle(OpiskelijanKaikkiTiedotResponse virtaXml, String frontendLog) {
        try {
            String anonymizedVirtaXml = virtaXml != null ? anonymize(virtaXml) : "EMPTY VIRTA XML";
            String dataToBeEncrypted = anonymizedVirtaXml + "\n" + frontendLog;
            generateEncryptedLog(dataToBeEncrypted);
        } catch (Exception e) {
            log.error("Anonymizer or encryption failure.", e);
        }
    }

    private String anonymize(OpiskelijanKaikkiTiedotResponse virtaXml) throws JAXBException, TransformerException, IOException {

        Source xmlSource = new StreamSource(new StringReader(stringify(virtaXml)));
        Source xsltSource = new StreamSource(anonymizeStylesheet.getInputStream());

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer(xsltSource);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        transformer.transform(xmlSource, result);

        return sw.toString();
    }

    private void generateEncryptedLog(String data) throws Exception {
        PublicKey publicKey = EncryptUtil.loadRSAPublicKey(publicKeyPath);

        // Generate AES Key
        SecretKey aesKey = generateAESKey();

        // Generate random IV
        byte[] iv = generateIV();

        // Encrypt the AES key with the RSA public key
        byte[] encryptedAesKey = encryptAESKeyWithRSA(aesKey, publicKey);

        byte[] encryptedData = EncryptUtil.encryptDataWithAES(data, aesKey, iv);

        UUID uuid = UUID.randomUUID();

        saveToFile(securePath + "encryptedData_" + uuid, encryptedData);
        saveToFile(securePath + "encryptedAesKey_" + uuid, encryptedAesKey);
        saveToFile(securePath + "iv_" + uuid, iv);
    }

    private String stringify(OpiskelijanKaikkiTiedotResponse virtaXml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Virta.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter out = new StringWriter();
        marshaller.marshal(virtaXml.getVirta(), out);
        return out.toString();
    }
}
