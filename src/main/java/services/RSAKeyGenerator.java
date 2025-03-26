/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author KOH HUI QING
 */
import java.security.*;
import java.util.Base64;

public class RSAKeyGenerator {
    public static void main(String[] args) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024); // key length

            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // encode
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // print generate key
            System.out.println("Public Key:");
            System.out.println("-----BEGIN PUBLIC KEY-----");
            System.out.println(publicKeyStr);
            System.out.println("-----END PUBLIC KEY-----");

            System.out.println("\nPrivate Key:");
            System.out.println("-----BEGIN PRIVATE KEY-----");
            System.out.println(privateKeyStr);
            System.out.println("-----END PRIVATE KEY-----");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}