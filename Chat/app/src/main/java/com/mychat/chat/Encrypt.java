package com.mychat.chat;

import android.os.Build;



import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class Encrypt {
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[]{'M', 'e', 'Z', 'O', 'b', 'l', 'o', 'g', 's', 'B', 'e', 's', 't', 'K', 'e', 'y'};


    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }


    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedData,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        //String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        // System.out.println("key= "+encodedKey);
        return key;
    }
}
