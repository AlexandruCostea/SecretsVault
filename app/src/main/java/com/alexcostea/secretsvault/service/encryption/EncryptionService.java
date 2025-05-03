package com.alexcostea.secretsvault.service.encryption;

import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;

import java.util.concurrent.CompletableFuture;

public interface EncryptionService {
    CompletableFuture<Void> reEncryptAllSecrets(EncryptionStrategy newStrategy) throws RuntimeException;
}