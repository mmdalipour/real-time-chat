package com.example.localx.androidproject.Encryption;
import android.util.Base64;
import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import static android.content.ContentValues.TAG;

public class SecurityKey {

    static final String RSA_MODE = "RSA/ECB/PKCS1Padding";

    private KeyPair keyPair;


    public SecurityKey(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public SecurityKey(PublicKey publicKey, PrivateKey privateKey) {
        this.keyPair = new KeyPair(publicKey,privateKey);
    }

    public String encrypt(String token) {
        if (token == null) return null;

        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            byte[] encrypted = cipher.doFinal(token.getBytes());
            return Base64.encodeToString(encrypted, Base64.URL_SAFE);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "encrypt: " + e.getMessage());
        }
        //Unable to encrypt Token
        return null;

    }

    public String decrypt(String encryptedToken) {
        if (encryptedToken == null) return null;

        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            byte[] decoded = Base64.decode(encryptedToken, Base64.URL_SAFE);
            byte[] original = cipher.doFinal(decoded);
            return new String(original);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "encrypt: " + e.getMessage());
        }
        //Unable to decrypt encrypted Token
        return null;
    }

    private Cipher getCipher(int mode) throws GeneralSecurityException {
        Cipher cipher;
        cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(mode, mode == Cipher.DECRYPT_MODE ? keyPair.getPrivate() : keyPair.getPublic());
        return cipher;
    }

}
