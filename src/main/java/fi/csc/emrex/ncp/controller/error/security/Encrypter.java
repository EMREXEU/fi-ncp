package fi.csc.emrex.ncp.controller.error.security;

import javax.crypto.SecretKey;
import java.security.PublicKey;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

public class Encrypter {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: Encrypter <file path> <public key>");
            System.exit(0);
        }

        String loadedDecryptedData = new String(loadFromFile(args[0]));
        PublicKey loadedPublicKey = loadRSAPublicKey(args[1]);

        // Generate AES Key
        SecretKey aesKey = generateAESKey();

        // Generate random IV
        byte[] iv = generateIV();

        // Encrypt the AES key with the RSA public key
        byte[] encryptedAesKey = encryptAESKeyWithRSA(aesKey, loadedPublicKey);

        // Encrypt the data with the AES key and IV
        byte[] encryptedData = encryptDataWithAES(loadedDecryptedData, aesKey, iv);

        // Save the encrypted data, encrypted AES key, and IV.
        saveToFile("encryptedData.enc", encryptedData);
        saveToFile("encryptedAesKey.enc", encryptedAesKey);
        saveToFile("iv.enc", iv);
    }
}
