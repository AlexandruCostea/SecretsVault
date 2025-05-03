package com.alexcostea.secretsvault.utils.encryption.strategies;
public interface EncryptionStrategy {
    void generateKeyIfNeeded() throws Exception;
    void deleteKey() throws Exception;
    byte[] encrypt(byte[] messageBytes) throws Exception;
    byte[] decrypt(byte[] ciphertext) throws Exception;
}
