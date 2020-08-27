package fr.mrcubee.bungeelink.security;

import net.md_5.bungee.BungeeCord;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Logger;

public class KeyUtils {

    public static PrivateKey loadPrivateKey(byte[] bytes) {
        KeyFactory keyFactory;
        PKCS8EncodedKeySpec spec;

        if (bytes == null)
            return null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            spec = new PKCS8EncodedKeySpec(bytes);
            return keyFactory.generatePrivate(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {}
        return null;
    }

    public static PrivateKey loadPrivateKey(URI uri) {
        if (uri == null)
            return null;
        try {
            return loadPrivateKey(Files.readAllBytes(Paths.get(uri)));
        } catch (IOException ignored) {}
        return null;
    }

    public static PrivateKey loadPrivateKey(URL url) {
        if (url == null)
            return null;
        try {
            return loadPrivateKey(url.toURI());
        } catch (URISyntaxException ignored) {}
        return null;
    }

    public static PublicKey loadPublicKey(byte[] bytes) {
        KeyFactory keyFactory;
        X509EncodedKeySpec spec;

        if (bytes == null)
            return null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            spec = new X509EncodedKeySpec(bytes);
            return keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {}
        return null;
    }

    public static PublicKey loadPublicKey(URI uri) {
        if (uri == null)
            return null;
        try {
            return loadPublicKey(Files.readAllBytes(Paths.get(uri)));
        } catch (IOException ignored) {}
        return null;
    }

    public static PublicKey loadPublicKey(URL url) {
        if (url == null)
            return null;
        try {
            return loadPublicKey(url.toURI());
        } catch (URISyntaxException ignored) {}
        return null;
    }

    public static byte[] createSignature(PrivateKey privateKey, byte[] bytes) {
        Signature signature;

        if (privateKey == null || bytes == null || bytes.length < 1)
            return null;
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(Base64.getEncoder().encode(bytes));
            return Base64.getEncoder().encode(signature.sign());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {}
        return null;
    }

    public static boolean checkSignature(PublicKey publicKey, byte[] signatureBytes, byte[] bytes) {
        Signature signature;
        
        if (publicKey == null || signatureBytes == null || signatureBytes.length < 1 || bytes == null || bytes.length < 1)
            return false;
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(Base64.getEncoder().encode(bytes));
            return signature.verify(Base64.getDecoder().decode(signatureBytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {}
        return false;
    }

}
