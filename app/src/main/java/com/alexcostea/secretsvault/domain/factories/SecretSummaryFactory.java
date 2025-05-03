package com.alexcostea.secretsvault.domain.factories;

import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.domain.entities.SecretSummary;

public class SecretSummaryFactory implements SecretFactory {
    @Override
    public Secret createSecret(Integer id, String... args) throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Missing secret title and last updated date");
        }

        String title = args[0];
        String updatedAt = args[1];

        return new SecretSummary(id, title, updatedAt);
    }

    @Override
    public Secret createSecret(String... args) throws IllegalArgumentException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Missing secret title");
        }

        String title = args[0];

        return new SecretSummary(title);
    }
}