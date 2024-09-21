package client_server.security;

import java.security.*;
import javax.crypto.Cipher;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Diese Klasse verwaltet ein RSA-Schlüsselpaar und bietet Methoden für Verschlüsselung,
 * Entschlüsselung, Signierung und Signaturverifizierung.
 */
public class KeyPairManager {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Konstruktor, der ein neues RSA-Schlüsselpaar generiert.
     *
     * @throws NoSuchAlgorithmException wenn der RSA-Algorithmus nicht verfügbar ist
     */
    public KeyPairManager() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    /**
     * Konvertiert einen öffentlichen Schlüssel in einen Base64-codierten String.
     *
     * @param publicKey der zu konvertierende öffentliche Schlüssel
     * @return Base64-codierte String-Repräsentation des öffentlichen Schlüssels
     */
    public static String publicKeyToString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    /**
     * Konvertiert einen Base64-codierten String zurück in einen öffentlichen Schlüssel.
     *
     * @param publicKeyString Base64-codierte String-Repräsentation des öffentlichen Schlüssels
     * @return der rekonstruierte öffentliche Schlüssel
     * @throws Exception wenn bei der Konvertierung ein Fehler auftritt
     */
    public static PublicKey stringToPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    /**
     * Verschlüsselt eine Nachricht mit dem öffentlichen Schlüssel.
     *
     * @param message die zu verschlüsselnde Nachricht
     * @param publicKey der öffentliche Schlüssel für die Verschlüsselung
     * @return Base64-codierte verschlüsselte Nachricht
     * @throws Exception wenn bei der Verschlüsselung ein Fehler auftritt
     */
    public static String encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Entschlüsselt eine verschlüsselte Nachricht mit dem privaten Schlüssel.
     *
     * @param encryptedMessage die verschlüsselte Nachricht als Base64-codierter String
     * @param privateKey der private Schlüssel für die Entschlüsselung
     * @return die entschlüsselte Nachricht
     * @throws Exception wenn bei der Entschlüsselung ein Fehler auftritt
     */
    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    /**
     * Signiert eine Nachricht mit dem privaten Schlüssel.
     *
     * @param message die zu signierende Nachricht
     * @param privateKey der private Schlüssel zum Signieren
     * @return die erzeugte Signatur als Byte-Array
     * @throws Exception wenn bei der Signierung ein Fehler auftritt
     */
    public static byte[] signMessage(String message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }

    /**
     * Verifiziert die Signatur einer Nachricht mit dem öffentlichen Schlüssel.
     *
     * @param message die ursprüngliche Nachricht
     * @param signature die zu verifizierende Signatur
     * @param publicKey der öffentliche Schlüssel zur Verifizierung
     * @return true, wenn die Signatur gültig ist, sonst false
     * @throws Exception wenn bei der Verifizierung ein Fehler auftritt
     */
    public static boolean verifySignature(String message, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(message.getBytes());
        return sig.verify(signature);
    }

    /**
     * Gibt den öffentlichen Schlüssel zurück.
     *
     * @return der öffentliche Schlüssel
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Gibt den privaten Schlüssel zurück.
     *
     * @return der private Schlüssel
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
