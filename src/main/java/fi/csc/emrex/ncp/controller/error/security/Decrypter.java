package fi.csc.emrex.ncp.controller.error.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static fi.csc.emrex.ncp.controller.error.security.EncryptUtil.*;

public class Decrypter {
    private static final Logger logger = LoggerFactory.getLogger(Decrypter.class.getName());
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: Decrypter <encrypted data directory path> <RSA private key> <output path>");
            System.exit(0);
        }

        String dirPath = args[0];
        String privateKeyPath = args[1];
        String outputPath = args[2];

        if (Files.isRegularFile(Path.of(outputPath))) {
            System.out.println("Output file already exists, cannot continue.");
            System.exit(0);
        }

        ArrayList<String> seenKeys = new ArrayList<>(100000);

        if (Files.isDirectory(Path.of(dirPath))) {
            System.out.println("Decrypting started...");
            try(FileWriter fileWriter = new FileWriter(outputPath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter out = new PrintWriter(bufferedWriter)) {

                List<Path> encryptedLogs = findEncryptedLogs(dirPath);
                System.out.println("Found " + encryptedLogs.size() + " encrypted logs.");

                for (Path log : encryptedLogs) {
                    // Decrypt
                    String key = log.getFileName().toString().split("_")[1].trim();
                    // Skip already seen keys.
                    if (!seenKeys.contains(key)) {
                        seenKeys.add(key);

                        String decryptedData = decrypt(key, dirPath, privateKeyPath);
                        out.println(decryptedData);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        System.out.println("Decrypting done...");
    }

    private static List<Path> findEncryptedLogs(String path) throws IOException {
        return Files.list(Path.of(path))
                .filter(isEncryptedLog())
                .toList();
    }

    private static Predicate<Path> isEncryptedLog() {
        return p -> p.getFileName().toString().startsWith("encryptedAesKey_")
                || p.getFileName().toString().startsWith("encryptedData_")
                || p.getFileName().toString().startsWith("iv_");
    }

    /**
     * Assume string data
     * @param key
     * @param dirPath
     * @param privateKeyPath
     * @return
     * @throws Exception
     */
    private static String decrypt(String key, String dirPath, String privateKeyPath) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Decrypting key: ").append(key);
        stringBuilder.append("\n");
        stringBuilder.append("--------------------------------------------------------------------");
        stringBuilder.append("\n");

        byte[] loadedEncryptedData = loadFromFile(dirPath + "encryptedData_" + key);
        byte[] loadedEncryptedAesKey = loadFromFile(dirPath + "encryptedAesKey_" + key);
        byte[] loadedIV = loadFromFile(dirPath + "iv_" + key);
        PrivateKey loadedRSAPrivateKey = loadRSAPrivateKey(privateKeyPath);

        SecretKey decryptedAesKey = decryptAESKeyWithRSA(loadedEncryptedAesKey, loadedRSAPrivateKey);
        String decryptedData = decryptDataWithAES(loadedEncryptedData, decryptedAesKey, loadedIV);

        stringBuilder.append(decryptedData);
        stringBuilder.append("\n");
        stringBuilder.append("--------------------------------------------------------------------");
        return stringBuilder.toString();
    }
}
