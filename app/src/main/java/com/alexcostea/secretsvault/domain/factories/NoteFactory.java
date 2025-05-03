package com.alexcostea.secretsvault.domain.factories;

import com.alexcostea.secretsvault.domain.entities.Note;
import com.alexcostea.secretsvault.domain.entities.Secret;

public class NoteFactory implements SecretFactory {
    @Override
    public Secret createSecret(Integer id, String... args) throws IllegalArgumentException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Missing note title, last updated date and content");
        }

        String title = args[0];
        String updatedAt = args[1];
        String content = args[2];

        return new Note(id, title, updatedAt, content);
    }

    @Override
    public Secret createSecret(String... args) throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Missing note title and content");
        }

        String title = args[0];
        String content = args[1];

        return new Note(title, content);
    }
}