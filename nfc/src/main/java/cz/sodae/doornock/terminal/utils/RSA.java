package cz.sodae.doornock.terminal.utils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    static final String ALGORITHM = "RSA";

    static final String ALGORITHM_SINGING = "SHA256withRSA";


    /**
     * Convert bytes which representing private key to java object
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(spec);
    }


    /**
     * Convert bytes which representing public key to java object
     */
    public static PublicKey getPublicKey(byte[] keyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePublic(spec);
    }


    /**
     * Sign data with private key by algorithm
     */
    public static byte[] sign(byte[] data, PrivateKey key)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signer = Signature.getInstance(ALGORITHM_SINGING);
        signer.initSign(key);
        signer.update(data);
        return signer.sign();
    }


    /**
     * Verify signed data if signature was really created by private key
     */
    public static boolean verify(byte[] originData, PublicKey key, byte[] encrypted)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signer = Signature.getInstance(ALGORITHM_SINGING);
        signer.initVerify(key);
        signer.update(originData);
        return signer.verify(encrypted);

    }


}
