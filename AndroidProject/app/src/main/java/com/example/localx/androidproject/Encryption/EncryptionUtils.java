package com.example.localx.androidproject.Encryption;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class EncryptionUtils {

    private static final String TAG = "EncryptionUtils";

    /**
     * Encrypt text using public_key
     * @param context
     * @param token
     * @return
     */
    public static String encrypt(Context context, String token) {
        SecurityKey securityKey = new SecurityKey(getKeyPair(context));
        return securityKey.encrypt(token);

    }

    /**
     * Decrypt text using private_key
     * @param context
     * @param token
     * @return
     */
    public static String decrypt(Context context, String token) {
        SecurityKey securityKey = new SecurityKey(getKeyPair(context));
        return securityKey.decrypt(token);
    }

    /**
     * Get key pair as public-key and private-key
     * @param context
     * @return
     */
    public static KeyPair getKeyPair(Context context) {
        return RSAKeyGenerator.generateKeyPairPreM(context, getKeyStore());
    }

    /**
     * Return key store
     * @return
     */
    private static KeyStore getKeyStore() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(RSAKeyGenerator.ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "getKeyStore: " + e.getMessage());
        }
        return keyStore;
    }

    /**
     * Clear all entries containing KEY_ALIAS
     */
    public static void clear() {
        KeyStore keyStore = getKeyStore();
        try {
            if (keyStore.containsAlias(RSAKeyGenerator.KEY_ALIAS)) {
                keyStore.deleteEntry(RSAKeyGenerator.KEY_ALIAS);
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, "getKeyStore: " + e.getMessage());
        }
    }

}
