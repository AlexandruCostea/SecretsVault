package com.alexcostea.secretsvault.service.secrets;

import com.alexcostea.secretsvault.domain.entities.Note;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.repository.secret.SecretRepository;
import com.alexcostea.secretsvault.utils.encryption.SecretsManager;
import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;

public class NoteSecretService extends AbstractSecretService {
    public NoteSecretService(SecretRepository noteRepository) {
        super(noteRepository);
    }

    @Override
    protected Secret encryptSecret(Secret secret) throws RuntimeException {
        Note note = (Note) secret;

        try {
            SecretsManager secretsManager = SecretsManager.getInstance();

            if (note.getContent() != null) {
                String encryptedContent = secretsManager.encrypt(note.getContent());
                note.setContent(encryptedContent);
            }

            return note;
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting note", e);
        }
    }

    @Override
    protected Secret decryptSecret(Secret secret) throws RuntimeException {
        Note note = (Note) secret;

        try {
            SecretsManager secretsManager = SecretsManager.getInstance();

            if (note.getContent() != null) {
                String decryptedContent = secretsManager.decrypt(note.getContent());
                note.setContent(decryptedContent);
            }
            return note;
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting note", e);
        }
    }

    @Override
    public Secret reEncryptSecret(Secret secret, EncryptionStrategy newStrategy) throws RuntimeException {
        Note note = (Note) secret;

        try {
            SecretsManager secretsManager = SecretsManager.getInstance();

            if (note.getContent() != null) {
                String reEncryptedContent = secretsManager.reEncrypt(note.getContent(), newStrategy);
                note.setContent(reEncryptedContent);
            }

            return note;
        } catch (Exception e) {
            throw new RuntimeException("Error re-encrypting note", e);
        }
    }
}