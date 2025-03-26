/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author joonx
 */
import java.math.BigInteger;
import java.util.Base64;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class PaillierService {

    // method to get the pailler public and private key
    public static class PaillierKeyPair {

        private final PaillierPublicKey publicKey;
        private final PaillierPrivateKey privateKey;

        public PaillierKeyPair(PaillierPublicKey publicKey, PaillierPrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public PaillierPublicKey getPublicKey() {
            return publicKey;
        }

        public PaillierPrivateKey getPrivateKey() {
            return privateKey;
        }
    }

    //public key
    public static class PaillierPublicKey {

        private final BigInteger n; // modulus of public key [p*q]
        private final BigInteger g; // generator [n+1]
        private final BigInteger nsquare;

        public PaillierPublicKey(BigInteger n, BigInteger g) {
            this.n = n;
            this.g = g;
            this.nsquare = n.multiply(n); // calculate [n*n]
        }

        // covert n and g to byte arrays, endode
        public String toString() {
            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(n.toByteArray().length + g.toByteArray().length)
                            .put(n.toByteArray())
                            .put(g.toByteArray())
                            .array()
            );
        }

        // reverse operation of tostring(), decode
        public static PaillierPublicKey fromString(String encoded) {
            byte[] decoded = Base64.getDecoder().decode(encoded);
            int mid = decoded.length / 2; // split into 2 to reconstruct n and g
            BigInteger n = new BigInteger(Arrays.copyOfRange(decoded, 0, mid));
            BigInteger g = new BigInteger(Arrays.copyOfRange(decoded, mid, decoded.length));
            return new PaillierPublicKey(n, g);
        }

        //used for subsequest encryption operation via public key
        public PaillierContext createContext() {
            return new PaillierContext(this);
        }
    }

    // private key
    public static class PaillierPrivateKey {

        private final BigInteger lambda; // lcm(p-1,q-1)
        private final BigInteger mu; // L(gλmod n2))- 1mod n
        private final PaillierPublicKey publicKey;

        public PaillierPrivateKey(BigInteger lambda, BigInteger mu, PaillierPublicKey publicKey) {
            this.lambda = lambda;
            this.mu = mu;
            this.publicKey = publicKey;
        }

        // encode
        public String toString() {
            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(lambda.toByteArray().length + mu.toByteArray().length)
                            .put(lambda.toByteArray())
                            .put(mu.toByteArray())
                            .array()
            );
        }

        // decode
        public static PaillierPrivateKey fromString(String encoded, PaillierPublicKey publicKey) {
            byte[] decoded = Base64.getDecoder().decode(encoded);
            int mid = decoded.length / 2;
            BigInteger lambda = new BigInteger(Arrays.copyOfRange(decoded, 0, mid));
            BigInteger mu = new BigInteger(Arrays.copyOfRange(decoded, mid, decoded.length));
            return new PaillierPrivateKey(lambda, mu, publicKey);
        }

        public PaillierPublicKey getPublicKey() {
            return publicKey;
        }

        // core for decrypt encrypted data
        public BigInteger decrypt(EncryptedNumber encrypted) {
            BigInteger c = encrypted.getCiphertext();
            BigInteger nsquare = publicKey.nsquare;

            //m = (c^lambda mod n^2) * u mod n
            BigInteger u = c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(publicKey.n);
            return u.multiply(mu).mod(publicKey.n);
        }

        public BigInteger getLambda() {
            return lambda;
        }

        public BigInteger getMu() {
            return mu;
        }

    }

    // encrypt and random generate key
    public static class PaillierContext {

        private final PaillierPublicKey publicKey;
        private final SecureRandom random;

        public PaillierContext(PaillierPublicKey publicKey) {
            this.publicKey = publicKey;
            this.random = new SecureRandom();
        }

        // core encrypt method
        public EncryptedNumber encrypt(BigInteger m) {
            BigInteger r; // random number
            do {
                // generate
                r = new BigInteger(publicKey.n.bitLength(), random);
            } while (r.compareTo(publicKey.n) >= 0); //make sure r less than n

            BigInteger nsquare = publicKey.nsquare;
            BigInteger c = publicKey.g.modPow(m, nsquare)
                    .multiply(r.modPow(publicKey.n, nsquare))
                    .mod(nsquare); // c = (g^m * r^n) mod (n^2)

            return new EncryptedNumber(this, c, 0);
        }
    }

    public static class EncryptedNumber {

        private final PaillierContext context;
        private final BigInteger ciphertext; //text after encrpt
        private final int exponent; // used for expend the encrypt area valae

        public EncryptedNumber(PaillierContext context, BigInteger ciphertext, int exponent) {
            this.context = context;
            this.ciphertext = ciphertext;
            this.exponent = exponent;
        }

        public BigInteger getCiphertext() {
            return ciphertext;
        }

        public int getExponent() {
            return exponent;
        }

        // ciphertext to base64 format
        public String serialize() {
            byte[] ciphertextBytes = ciphertext.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(ciphertextBytes.length + 4);
            buffer.put(ciphertextBytes);
            buffer.putInt(exponent);
            return Base64.getEncoder().encodeToString(buffer.array());
        }

        // Paillier Addition method
        public EncryptedNumber add(EncryptedNumber other) {
            if (this.exponent != other.exponent) {
                throw new IllegalArgumentException("Exponents must match for addition");
            }
            BigInteger sum = this.ciphertext
                    .multiply(other.ciphertext)
                    .mod(context.publicKey.nsquare);
            return new EncryptedNumber(context, sum, this.exponent);
        }

        // from base64 to ciphertext, decoded
        public static EncryptedNumber deserialize(PaillierContext context, String serialized) {
            byte[] decoded = Base64.getDecoder().decode(serialized);

            // parsing process
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] ciphertextBytes = new byte[decoded.length - 4]; // remove last 4 byte [reserved for the exponent]
            buffer.get(ciphertextBytes);
            // convert
            BigInteger ciphertext = new BigInteger(ciphertextBytes);
            int exponent = buffer.getInt();

            return new EncryptedNumber(context, ciphertext, exponent);
        }

    }

    // generate new key 
    public static PaillierKeyPair generateKeyPair(int bitLength) {
        SecureRandom r = new SecureRandom(); // random number
        // generate 2 large prime number
        BigInteger p = BigInteger.probablePrime(bitLength / 2, r);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, r);

        BigInteger n = p.multiply(q); // n=p*q
        BigInteger g = n.add(BigInteger.ONE); // g=n+1

        // l=(p-1)(q-1) for private key, gcd for normalization
        BigInteger lambda = p.subtract(BigInteger.ONE)
                .multiply(q.subtract(BigInteger.ONE))
                .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        BigInteger nsquare = n.multiply(n); // n*n
        // l(gλmodn2))-1modn
        BigInteger mu = g.modPow(lambda, nsquare)
                .subtract(BigInteger.ONE)
                .divide(n)
                .modInverse(n);

        // generate key pair
        PaillierPublicKey publicKey = new PaillierPublicKey(n, g);
        PaillierPrivateKey privateKey = new PaillierPrivateKey(lambda, mu, publicKey);

        return new PaillierKeyPair(publicKey, privateKey);
    }

    public static boolean verifyKeyPair(PaillierKeyPair keyPair) {
        PaillierPublicKey publicKey = keyPair.getPublicKey();
        PaillierPrivateKey privateKey = keyPair.getPrivateKey();

        // Create a random message to test
        BigInteger message = new BigInteger(publicKey.n.bitLength(), new SecureRandom()).mod(publicKey.n);

        // Encrypt the message using the public key
        PaillierContext context = publicKey.createContext();
        EncryptedNumber encrypted = context.encrypt(message);

        // Decrypt the message using the private key
        BigInteger decrypted = privateKey.decrypt(encrypted);

        // Check if the decrypted message matches the original message
        return message.equals(decrypted);
    }
}
