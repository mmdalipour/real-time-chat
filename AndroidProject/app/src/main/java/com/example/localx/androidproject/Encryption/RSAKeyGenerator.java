package com.example.localx.androidproject.Encryption;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import static android.content.ContentValues.TAG;


public class RSAKeyGenerator {

    static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    static final String KEY_ALIAS = "RTChat_RSA_Key";


    /**
     * Generate a Rsa key pair with one year expiration date [ Private-Key can not be extracted ]
     * @param context
     * @param keyStore
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static KeyPair generateKeyPairPreM(Context context, KeyStore keyStore) {
        try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                //1 Year validity
                end.add(Calendar.YEAR, 1);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
                kpg.initialize(spec);
                kpg.generateKeyPair();
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            Log.e(TAG, "encrypt: " + e.getMessage() );
        }

        try {
            final KeyStore.PrivateKeyEntry entry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            return new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            Log.e(TAG, "encrypt: "+ e.getMessage() );
        }
        return null;
    }

}