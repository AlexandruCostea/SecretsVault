package com.alexcostea.secretsvault.repository.secret;

import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.utils.sql.FullUpdateQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SecretRepository {

    CompletableFuture<List<Secret>> getSecretPreviews() throws RuntimeException;

    CompletableFuture<List<Secret>> getFullSecrets() throws RuntimeException;

    CompletableFuture<Secret> getFullSecret(Integer id) throws RuntimeException;

    CompletableFuture<Secret> addSecret(Secret secret) throws RuntimeException;

    CompletableFuture<Secret> updateSecret(Secret secret) throws RuntimeException;

    CompletableFuture<Void> deleteSecret(Integer id) throws RuntimeException;

    FullUpdateQuery getFullUpdateSQL(List<Secret> secrets) throws RuntimeException;
}
