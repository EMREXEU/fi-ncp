package fi.csc.emrex.ncp;

import org.springframework.util.Base64Utils;
import org.w3c.dom.Document;

import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
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
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Created by marko.hollanti on 06/10/15.
 */
public class DataSign {

    public String sign(String certificate, String encKey, String data) throws Exception {

        // Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Create a Reference to the enveloped document (in this case, you are signing the whole
        // document, so a URI of "" signifies that, and also specify the SHA1 digest algorithm
        // and the ENVELOPED Transform.
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(
                        fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);

        // Create the SignedInfo.
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod
                        (CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        InputStream is = new ByteArrayInputStream(data.getBytes()); // StandardCharsets.ISO_8859_1
        Document doc = dbf.newDocumentBuilder().parse(is);

        // Extract the private key from string
        encKey = encKey.replaceAll("(-----.*?-----)", "");

        byte[] decoded = DatatypeConverter.parseBase64Binary(encKey);

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
    }

    private static X509Certificate getCertificate(String certString) throws IOException, GeneralSecurityException {
        InputStream is = new ByteArrayInputStream(certString.getBytes());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        is.close();
        return cert;
    }

}
