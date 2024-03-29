package fi.csc.emrex.ncp.service;

import fi.csc.emrex.ncp.exception.NcpException;
import fi.csc.emrex.ncp.util.GzipUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jakarta.xml.bind.DatatypeConverter;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Created by marko.hollanti on 06/10/15.
 */
@Setter
@Slf4j
@Service
public class DataSignService {

  private String certificate;
  private String encryptionKey;

  @Value("${path.certificate}")
  private String certificatePath;

  @Value("${ncp.path.encryption.key}")
  private String encryptionKeyPath;

  @Value("${environment}")
  private String environment;

  public String sign(String data, Charset charset) throws NcpException {
    try {
      assertCertificateAndEncryptionKeyAvailable();

      // Create a DOM XMLSignatureFactory that will be used to generate the enveloped
      // signature.
      XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

      // Create a Reference to the enveloped document (in this case, you are signing
      // the whole
      // document, so a URI of "" signifies that, and also specify the SHA1 digest
      // algorithm
      // and the ENVELOPED Transform.
      Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
          Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);

      // Create the SignedInfo.
      SignedInfo si = fac.newSignedInfo(
          fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
          fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

      // Instantiate the document to be signed.
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      InputStream is = new ByteArrayInputStream(data.getBytes(charset)); // StandardCharsets.ISO_8859_1
      Document doc = dbf.newDocumentBuilder().parse(is);

      // Extract the private key from string
      encryptionKey = encryptionKey.replaceAll("(-----.*?-----)", "");

      byte[] decoded = DatatypeConverter.parseBase64Binary(encryptionKey);

      PKCS8EncodedKeySpec rsaPrivKeySpec = new PKCS8EncodedKeySpec(decoded);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      RSAPrivateKey pk = (RSAPrivateKey) kf.generatePrivate(rsaPrivKeySpec);

      // Create a DOMSignContext and specify the RSA PrivateKey and
      // location of the resulting XMLSignature's parent element.
      DOMSignContext dsc = new DOMSignContext(pk, doc.getDocumentElement());

      // Create the XMLSignature, but don't sign it yet.
      KeyInfoFactory kif = fac.getKeyInfoFactory();
      X509Certificate cert = getCertificate(certificate);
      List<Object> x509Content = new ArrayList<Object>();

      x509Content.add(cert.getSubjectX500Principal().getName());
      x509Content.add(cert);
      X509Data xd = kif.newX509Data(x509Content);
      KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
      XMLSignature signature = fac.newXMLSignature(si, ki);

      // Marshal, generate, and sign the enveloped signature.
      signature.sign(dsc);

      OutputStream os = new ByteArrayOutputStream();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer trans = tf.newTransformer();
      trans.transform(new DOMSource(doc), new StreamResult(os));

      final String signedXml = os.toString();
      final byte[] compressedXml = GzipUtil.compress(signedXml);

      return DatatypeConverter.printBase64Binary(compressedXml);
    } catch (Exception e) {
      throw new NcpException("Signing failed.", e);
    }
  }

  private void assertCertificateAndEncryptionKeyAvailable() throws Exception {
    if (certificate == null) {
      certificate = readFileContent(certificatePath);
    }
    if (encryptionKey == null) {
      encryptionKey = readFileContent(encryptionKeyPath);
    }
  }

  private String readFileContent(String path) throws Exception {
    return new String(Files.readAllBytes(Paths.get(path)));
  }

  private static X509Certificate getCertificate(String certString) throws IOException, GeneralSecurityException {
    InputStream is = new ByteArrayInputStream(certString.getBytes());
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
    is.close();
    return cert;
  }

  public boolean verifySignature(String data) throws Exception {
    assertCertificateAndEncryptionKeyAvailable();

    // Create a DOM XMLSignatureFactory that will be used to generate the enveloped
    // signature.
    XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

    // Instantiate the document to be signed.
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)); // StandardCharsets.ISO_8859_1
    Document doc = dbf.newDocumentBuilder().parse(is);

    // Find Signature element.
    NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
    if (nl.getLength() == 0) {
      throw new Exception("Cannot find Signature element");
    }

    X509Certificate cert = getCertificate(certificate);
    PublicKey pubKey = cert.getPublicKey();
    DOMValidateContext valContext = new DOMValidateContext(pubKey, nl.item(0));

    // Unmarshal the XMLSignature.
    XMLSignature signature = fac.unmarshalXMLSignature(valContext);

    // Validate the XMLSignature.
    boolean coreValidity = signature.validate(valContext);

    // Check core validation status.
    if (coreValidity == false) {
      log.error("Signature failed core validation");
      boolean sv = signature.getSignatureValue().validate(valContext);
      log.error("signature validation status: " + sv);
      if (sv == false) {
        // Check the validation status of each Reference.
        Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
        for (int j = 0; i.hasNext(); j++) {
          boolean refValid = ((Reference) i.next()).validate(valContext);
          System.out.println("ref[" + j + "] validity status: " + refValid);
        }
      }
    }

    return coreValidity;
  }

  public String decodeAndDecompress(String data) throws IOException {
    final byte[] decoded = DatatypeConverter.parseBase64Binary(data);

    final byte[] decompressed = GzipUtil.gzipDecompressBytes(decoded);

    final String s = new String(decompressed);
    // log.info("XML: {}", s);

    return s;
  }

}
