/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author joonx
 */
public class TestFunction {
public static void main(String[] args) {
        try {
            // Test 2: Longer string
            testEncryptDecrypt("This is a longxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxgfgvghvghvghvghnvnmvhvMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuIGJrjr7+c3ACmTMVGlDepcrBr9WSuLlIKu0u3rCu62G9fGub8jB26ZkxM9W8CDZvJiv3GLZ129h14mo6jnSN13oGDh4p5LCV6U7aq1F8PHJlsOUGnK5ErB2c/HZulbhpcuk+FjLqHv8CF1cY/lPm8vw3T/QXEq227fKFJDnfKbwwQ6LKSXMlau0AJI6At+EhB11y8yf7Kf4TOdCvt33SUAJ5POAVVPs4Yro02zNB0VmaKmTs0irdtMhg1s6+5CuD9FyXsAyq30PYdV9QGY5LNLyXiqyR4fr9ilfqnpcmKbmU4oqQL7FiXqDMDA+7QfmqZZgeEb2IF+iieXMmEl4kwIDAQABxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxer test string to verify encryption and decryption");
            
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testEncryptDecrypt(String original) throws Exception {
        System.out.println("\n=== Testing with: " + original + " ===");
        
        // Encrypt
        String encrypted = RSAUtil.encrypt(original);
        System.out.println("Encrypted: " + encrypted);
        
        // Decrypt
        String decrypted = RSAUtil.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        
        // Verify
        boolean success = original.equals(decrypted);
        System.out.println("Test " + (success ? "PASSED" : "FAILED"));
        if (!success) {
            System.out.println("Original length: " + original.length());
            System.out.println("Decrypted length: " + decrypted.length());
        }
    }
}