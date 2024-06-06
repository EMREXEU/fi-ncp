package fi.csc.emrex.ncp.controller.error.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

public class KeyGen {
    public static void main(String[] args) throws Exception {

        // Generate RSA key pair
        KeyPair keyPair = generateRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyBase64 = new String(Base64.getEncoder().encode(privateKey.getEncoded()), StandardCharsets.ISO_8859_1);

        saveToFile("publicKeyEmrex.pub", publicKey.getEncoded());
        saveToFile("privateKeyEmrex", privateKeyBase64);
    }
}
