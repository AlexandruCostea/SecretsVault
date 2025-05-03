package com.alexcostea.secretsvault.service.secrets;

import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.repository.secret.SecretRepository;
import com.alexcostea.secretsvault.utils.sql.FullUpdateQuery;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractSecretService implements SecretService {

    protected final SecretRepository repository;

    protected AbstractSecretService(SecretRepository repository) {
        this.repository = repository;
    }

    protected abstract Secret encryptSecret(Secret secret) throws RuntimeException;

    protected abstract Secret decryptSecret(Secret secret) throws RuntimeException;


    @Override
    public CompletableFuture<List<Secret>> getSecretPreviews() throws RuntimeException {
        return repository.getSecretPreviews();
    }

    @Override
    public CompletableFuture<List<Secret>> getFullSecrets() throws RuntimeException {
        return repository.getFullSecrets();
    }

    @Override
    public CompletableFuture<Secret> getFullSecret(Integer id) throws RuntimeException {
        return repository.getFullSecret(id)
                .thenApply(this::decryptSecret);
    }

    @Override
    public CompletableFuture<Secret> addSecret(Secret secret) throws RuntimeException {
        Secret encryptedSecret = encryptSecret(secret);
        return repository.addSecret(encryptedSecret);
    }

    @Override
    public CompletableFuture<Secret> updateSecret(Secret secret) throws RuntimeException {
        Secret encryptedSecret = encryptSecret(secret);
        return repository.updateSecret(encryptedSecret);
    }

    @Override
    public CompletableFuture<Void> deleteSecret(Integer id) throws RuntimeException {
        return repository.deleteSecret(id);
    }

    @Override
    public FullUpdateQuery getBatchUpdateSQL(List<Secret> secrets) throws RuntimeException {
        return repository.getFullUpdateSQL(secrets);
    }
}