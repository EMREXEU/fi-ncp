package fi.csc.emrex.ncp.controller.error.security;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

public class Decrypter {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: Decrypter <encrypted data file path> <encrypted aes key> <iv> <RSA private key>");
            System.exit(0);
        }
        byte[] loadedEncryptedData = loadFromFile(args[0]);
        byte[] loadedEncryptedAesKey = loadFromFile(args[1]);
        byte[] loadedIV = loadFromFile(args[2]);
        PrivateKey loadedRSAPrivateKey = loadRSAPrivateKey(args[3]);

        SecretKey decryptedAesKey = decryptAESKeyWithRSA(loadedEncryptedAesKey, loadedRSAPrivateKey);
        String decryptedData = decryptDataWithAES(loadedEncryptedData, decryptedAesKey, loadedIV);

        System.out.println(decryptedData);
    }
}
