package com.alexcostea.secretsvault.utils.encryption;

import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecretsManager {
    private static SecretsManager instance;
    private EncryptionStrategy strategy;

    private SecretsManager(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    public static synchronized SecretsManager getInstance(EncryptionStrategy strategy) {
        if (instance == null) {
            instance = new SecretsManager(strategy);
        }
        return instance;
    }

    public static synchronized SecretsManager getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("SecretsManager not initialized.");
        }
        return instance;
    }

    public EncryptionStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(EncryptionStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    public String encrypt(String plainText) throws Exception {
        byte[] messageBytes = plainText.getBytes(StandardCharsets.UTF_8);

        byte[] result = strategy.encrypt(messageBytes);

        return Base64.getEncoder().encodeToString(result);
    }

    public String decrypt(String cipherText) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherText);

        byte[] decryptedBytes = this.strategy.decrypt(combined);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String reEncrypt(String cipherText, EncryptionStrategy newStrategy) throws Exception {
        byte[] combined = Base64.getDecoder().decode(cipherText);

        byte[] decryptedBytes = this.strategy.decrypt(combined);

        byte[] newCiphertext = newStrategy.encrypt(decryptedBytes);

        return Base64.getEncoder().encodeToString(newCiphertext);
    }
}
