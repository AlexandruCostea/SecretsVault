package com.alexcostea.secretsvault.service.secrets;

import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;
import com.alexcostea.secretsvault.utils.sql.FullUpdateQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SecretService {

    CompletableFuture<List<Secret>> getSecretPreviews() throws RuntimeException;
    CompletableFuture<List<Secret>> getFullSecrets() throws RuntimeException;
    CompletableFuture<Secret> getFullSecret(Integer id) throws RuntimeException;
    CompletableFuture<Secret> addSecret(Secret secret) throws RuntimeException;
    CompletableFuture<Secret> updateSecret(Secret secret) throws RuntimeException;
    CompletableFuture<Void> deleteSecret(Integer id) throws RuntimeException;
    Secret reEncryptSecret(Secret secret, EncryptionStrategy newStrategy) throws RuntimeException;
    FullUpdateQuery getBatchUpdateSQL(List<Secret> secrets) throws RuntimeException;
}
