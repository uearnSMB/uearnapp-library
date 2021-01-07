package smarter.uearn.money.chats.utils;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

    public static BigInteger publicModulus;
    public static BigInteger publicExponent;

    public static BigInteger privateModulus;
    public static BigInteger privateExponent;


    /* Generationg Rsa key public and private modulus and exponetial and saving them
     * i am saving them here as static variable
     * for future refrence you can also save them anywhere else you like*/
    public static void generateAndSaveRsaKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048); // you can decrease and increase this as per requirement
        KeyPair kp = kpg.genKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
                RSAPrivateKeySpec.class);

        publicModulus = pub.getModulus();
        publicExponent = pub.getPublicExponent();
        privateModulus = priv.getModulus();
        privateExponent = priv.getPrivateExponent();

    }


    public static PublicKey readPublicKey() throws IOException {

        try {

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(publicModulus, publicExponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        }
    }

    public static PrivateKey readPrivateKey() throws IOException {

        try {

            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(privateModulus, privateExponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey priKey = fact.generatePrivate(keySpec);
            return priKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        }
    }

    public static String rsaEncrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        PrivateKey pubKey = readPrivateKey();
        Log.e("RSA","pubKey :: "+pubKey.getAlgorithm() + " :: "+pubKey.getFormat() + " :: "+pubKey.toString());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(cipherData,Base64.NO_WRAP);
    }

    public static String rsaDecrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PublicKey pubKey = readPublicKey();

        Log.e("RSA","privateKey :: "+pubKey.getAlgorithm() + " :: "+pubKey.getFormat() + " :: "+pubKey.toString());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(Base64.decode(data, Base64.NO_WRAP));
        return new String(cipherData);
    }
}
