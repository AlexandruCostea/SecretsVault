package com.alexcostea.secretsvault.repository.user;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
    CompletableFuture<Optional<String>> getKeyAlias() throws RuntimeException;

    CompletableFuture<Void> setKeyAlias(String keyAlias) throws RuntimeException;
}
