package com.alexcostea.secretsvault.utils.encryption.strategies;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESEncryptionStrategy implements EncryptionStrategy {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final int IV_LENGTH = 16;
    private final String keyAlias;

    public AESEncryptionStrategy(String keyAlias) {
        this.keyAlias = keyAlias;
    }


    @Override
    public void generateKeyIfNeeded() throws Exception {
        KeyStore keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keystore.load(null);
        if (!keystore.containsAlias(this.keyAlias)) {
            generateNewKey();
        }
    }

    private void generateNewKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE);

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                this.keyAlias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false)
//                .setUserAuthenticationValidityDurationSeconds(300)
                .build();

        generator.init(spec);
        generator.generateKey();
    }

    @Override
    public void deleteKey() throws Exception {
        KeyStore keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keystore.load(null);
        if (keystore.containsAlias(this.keyAlias)) {
            keystore.deleteEntry(this.keyAlias);
        }
    }

    private SecretKey getKey() throws Exception {
        KeyStore keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keystore.load(null);
        return (SecretKey) keystore.getKey(this.keyAlias, null);
    }

    @Override
    public byte[] encrypt(byte[] messageBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(messageBytes);

        byte[] ivAndCiphertext = new byte[IV_LENGTH + ciphertext.length];
        System.arraycopy(iv, 0, ivAndCiphertext, 0, IV_LENGTH);
        System.arraycopy(ciphertext, 0, ivAndCiphertext, IV_LENGTH, ciphertext.length);

        return ivAndCiphertext;
    }

    @Override
    public byte[] decrypt(byte[] ivAndCiphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        byte[] iv = Arrays.copyOfRange(ivAndCiphertext, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(ivAndCiphertext, IV_LENGTH, ivAndCiphertext.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, getKey(), ivSpec);

        return cipher.doFinal(ciphertext);
    }

}
