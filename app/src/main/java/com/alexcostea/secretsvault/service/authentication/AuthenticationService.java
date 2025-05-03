package com.alexcostea.secretsvault.service.authentication;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {
    void setup() throws RuntimeException;
    CompletableFuture<Void> login() throws RuntimeException;
}
