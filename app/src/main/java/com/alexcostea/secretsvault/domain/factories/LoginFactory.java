package com.alexcostea.secretsvault.domain.factories;

import com.alexcostea.secretsvault.domain.entities.Login;
import com.alexcostea.secretsvault.domain.entities.Secret;

public class LoginFactory implements SecretFactory {
    @Override
    public Secret createSecret(Integer id, String... args) throws IllegalArgumentException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Missing password title, last updated date, mail/username and password");
        }

        String title = args[0];
        String updatedAt = args[1];
        String mailOrUsername = args[2];
        String password = args[3];

        return new Login(id, title, updatedAt, mailOrUsername, password);
    }

    @Override
    public Secret createSecret(String... args) throws IllegalArgumentException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Missing password title, mail/username and password");
        }

        String title = args[0];
        String mailOrUsername = args[1];
        String password = args[2];

        return new Login(title, mailOrUsername, password);
    }
}