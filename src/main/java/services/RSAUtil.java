/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author KOH HUI QING
 */
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.BadPaddingException;

public class RSAUtil {

    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJMR0sEO7pIdHfzN9YyGObeTjd7cQEuGUVcphL1hv7uTdtHgKXmtpBw/hstMufISpeQEzWsKuKFtgdRXmjdjRMXjN8onS3U5U/Lah/o0xM8dMktkx32+0OOZex4Rqfzvj8S+Mj0OTK48udEFmKuzFy+RrEXJRq78xyb2e3MfckMXAgMBAAECgYAV1c4ZeBWR9P0S9SM792ESBo1y5UwtymXrj9BTlnPd2MbfNy2r5qIYcugY6ViBfnpJxdrCcJI5SWW+uI4+zfVo7+C354735j41rG/y3ZRjh/zNjm7EFd4aq/ala5RfZAYCDEZTX6ViWT+naDQY2MkhpEBPRi/GyZjpyA/5sAb68QJBAKonnX6p+qPgMcabVtH0ZtiavuyZ+TQ/c+rSiIM91O3+Qlfq3je7ByK+rVz4m0GSePCVG1TYEXUplWfPcIBWakcCQQDdRKRljXVFvA5cAGgUCjPx89WCJSnyw3spDp8eUkJictfgn4/T91zY1CuRjkOlSKU19k3XffWH8GiZCAFOUvixAkA1wGP5YnsCD83JnsLTenCO1nufxJg70BuvP6jORNciA6MzoMdoNZXwfOnJJ+Pr1/iKj5/dso/5QhSngZlZgXL7AkBR+LcKpDA46GVNch0cp3b9AL8tWsVCmWbMHA2YtxK+1BrjrVkkRrSP43JMly66UcJazmeiGJjYhpjJcKzDLnvRAkAIEfRzlsuWAz4UnzXyeVPC0pyPp4KZHZfaJpLhpA9v7/BuG+BdzZqd8Z9A84litdpx0zSnSF2z13XqnkgnjJ2Z";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTEdLBDu6SHR38zfWMhjm3k43e3EBLhlFXKYS9Yb+7k3bR4Cl5raQcP4bLTLnyEqXkBM1rCrihbYHUV5o3Y0TF4zfKJ0t1OVPy2of6NMTPHTJLZMd9vtDjmXseEan874/EvjI9DkyuPLnRBZirsxcvkaxFyUau/Mcm9ntzH3JDFwIDAQAB";

    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            throw new IllegalArgumentException("Encrypted data cannot be null or empty");
        }

        try {
            System.out.println("Starting decryption of data length: " + encryptedData.length());
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            System.out.println("Decoded Base64 length: " + encryptedBytes.length + " bytes");

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PrivateKey privateKey = getPrivateKey();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int dataLength = encryptedBytes.length;
            int blockCount = (dataLength + MAX_DECRYPT_BLOCK - 1) / MAX_DECRYPT_BLOCK;
            
            System.out.println("Total blocks to process: " + blockCount);

            for (int i = 0; i < blockCount; i++) {
                int offset = i * MAX_DECRYPT_BLOCK;
                int blockSize = Math.min(MAX_DECRYPT_BLOCK, dataLength - offset);
                
                System.out.println("Processing block " + (i + 1) + " of " + blockCount);
                System.out.println("Offset: " + offset + ", Block size: " + blockSize);

                try {
                    byte[] decryptedBlock = cipher.doFinal(
                        encryptedBytes, 
                        offset, 
                        blockSize
                    );
                    outputStream.write(decryptedBlock);
                    System.out.println("Successfully decrypted block " + (i + 1));
                } catch (BadPaddingException e) {
                    System.err.println("Bad padding in block " + (i + 1));
                    throw e;
                }
            }

            byte[] decryptedBytes = outputStream.toByteArray();
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("Decryption completed. Result length: " + result.length() + " characters");
            return result;

        } catch (Exception e) {
            System.err.println("Decryption error details: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Decryption failed: " + e.getMessage(), e);
        }
    }

    public static String encrypt(String data) throws Exception {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }

        try {
            System.out.println("Starting encryption of data: " + data);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            System.out.println("Input data length: " + dataBytes.length + " bytes");

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey publicKey = getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Encrypt in a single block if possible
            if (dataBytes.length <= MAX_ENCRYPT_BLOCK) {
                byte[] encryptedBytes = cipher.doFinal(dataBytes);
                String result = Base64.getEncoder().encodeToString(encryptedBytes);
                System.out.println("Encrypted in single block. Output length: " + result.length());
                return result;
            }

            // Otherwise split into blocks
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int dataLength = dataBytes.length;
            int blockCount = (dataLength + MAX_ENCRYPT_BLOCK - 1) / MAX_ENCRYPT_BLOCK;
            
            System.out.println("Data requires " + blockCount + " blocks");

            for (int i = 0; i < blockCount; i++) {
                int offset = i * MAX_ENCRYPT_BLOCK;
                int blockSize = Math.min(MAX_ENCRYPT_BLOCK, dataLength - offset);
                
                System.out.println("Processing block " + (i + 1) + " of " + blockCount);
                System.out.println("Offset: " + offset + ", Block size: " + blockSize);

                byte[] encryptedBlock = cipher.doFinal(dataBytes, offset, blockSize);
                outputStream.write(encryptedBlock);
            }

            byte[] encryptedBytes = outputStream.toByteArray();
            String result = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Encryption completed. Output length: " + result.length());
            return result;

        } catch (Exception e) {
            System.err.println("Encryption error details: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Encryption failed: " + e.getMessage(), e);
        }
    }

    private static PrivateKey getPrivateKey() throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new Exception("Failed to load private key: " + e.getMessage(), e);
        }
    }

    private static PublicKey getPublicKey() throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new Exception("Failed to load public key: " + e.getMessage(), e);
        }
    }
}