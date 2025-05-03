package com.alexcostea.secretsvault.domain.factories;
import com.alexcostea.secretsvault.domain.entities.Secret;

public interface SecretFactory {
    Secret createSecret(Integer id, String... args) throws IllegalArgumentException;
    Secret createSecret(String... args) throws IllegalArgumentException;
}
