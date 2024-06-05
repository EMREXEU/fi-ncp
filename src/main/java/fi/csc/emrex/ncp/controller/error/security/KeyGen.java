package fi.csc.emrex.ncp.controller.error.security;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

public class KeyGen {
    public static void main(String[] args) throws Exception {

        // Generate RSA key pair
        KeyPair keyPair = generateRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        saveToFile("publicKeyEmrex.pub", publicKey.getEncoded());
        saveToFile("privateKeyEmrex", privateKey.getEncoded());
    }
}
