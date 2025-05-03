package com.alexcostea.secretsvault.service.secrets;

import com.alexcostea.secretsvault.domain.entities.Login;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.repository.secret.SecretRepository;
import com.alexcostea.secretsvault.utils.encryption.SecretsManager;
import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;

public class LoginSecretService extends AbstractSecretService {

    public LoginSecretService(SecretRepository loginRepository) {
        super(loginRepository);
    }

    @Override
    protected Secret encryptSecret(Secret secret) throws RuntimeException {
        Login login = (Login) secret;

        try {
             SecretsManager secretsManager = SecretsManager.getInstance();

            if (login.getMailOrUsername() != null) {
                String encryptedMailOrUsername = secretsManager.encrypt(login.getMailOrUsername());
                login.setMailOrUsername(encryptedMailOrUsername);
            }

            if (login.getPassword() != null) {
                String encryptedPassword = secretsManager.encrypt(login.getPassword());
                login.setPassword(encryptedPassword);
            }
            return login;
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting login", e);
        }
    }

    @Override
    protected Secret decryptSecret(Secret secret) throws RuntimeException {
        Login login = (Login) secret;

        try {
            SecretsManager secretsManager = SecretsManager.getInstance();

            if (login.getMailOrUsername() != null) {
                String decryptedMailOrUsername = secretsManager.decrypt(login.getMailOrUsername());
                login.setMailOrUsername(decryptedMailOrUsername);
            }

            if (login.getPassword() != null) {
                String decryptedPassword = secretsManager.decrypt(login.getPassword());
                login.setPassword(decryptedPassword);
            }
            return login;
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting login", e);
        }
    }

    @Override
    public Secret reEncryptSecret(Secret secret, EncryptionStrategy newStrategy) throws RuntimeException {
        Login login = (Login) secret;

        try {
            SecretsManager secretsManager = SecretsManager.getInstance();

            if (login.getMailOrUsername() != null) {
                String reEncryptedMailOrUsername = secretsManager.reEncrypt(login.getMailOrUsername(), newStrategy);
                login.setMailOrUsername(reEncryptedMailOrUsername);
            }
            if (login.getPassword() != null) {
                String reEncryptedPassword = secretsManager.reEncrypt(login.getPassword(), newStrategy);
                login.setPassword(reEncryptedPassword);
            }

            return login;
        } catch (Exception e) {
            throw new RuntimeException("Error re-encrypting login", e);
        }
    }
}
