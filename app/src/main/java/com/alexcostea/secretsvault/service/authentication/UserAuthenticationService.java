package com.alexcostea.secretsvault.service.authentication;

import com.alexcostea.secretsvault.repository.user.UserRepository;
import com.alexcostea.secretsvault.service.encryption.EncryptionService;
import com.alexcostea.secretsvault.utils.encryption.registry.EncryptionStrategyRegistry;
import com.alexcostea.secretsvault.utils.encryption.SecretsManager;
import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;

import java.sql.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private static final int ENCRYPTION_ROTATION_THRESHOLD_DAYS = 30;

    public UserAuthenticationService(
            UserRepository userRepository,
            EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public void setup() throws RuntimeException {
        try {
            var encryptionStrategyRegistry = EncryptionStrategyRegistry.getRegistry();
            String defaultStrategyId = EncryptionStrategyRegistry.getDefaultStrategyId();
            String initialKeyAlias = "key_" + defaultStrategyId + "_" + new Date(System.currentTimeMillis());
            EncryptionStrategy strategy = encryptionStrategyRegistry
                    .get(defaultStrategyId)
                    .apply(initialKeyAlias);

            strategy.generateKeyIfNeeded();
            SecretsManager.getInstance(strategy);
            userRepository.setKeyAlias(initialKeyAlias).join();
        } catch (Exception e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Override
    public CompletableFuture<Void> login() {
        return CompletableFuture.runAsync(() -> {
            try {
                Optional<String> keyAliasOptional = userRepository.getKeyAlias().join();
                if (keyAliasOptional.isEmpty()) {
                    this.setup();
                    return;
                }
                String keyAlias = keyAliasOptional.get();
                String[] keyAliasParts = keyAlias.split("_");

                String strategyId = keyAliasParts[1];
                String dateString = keyAliasParts[2];
                Date encryptionDate = Date.valueOf(dateString);
                var encryptionStrategyRegistry = EncryptionStrategyRegistry.getRegistry();

                EncryptionStrategy existingStrategy = encryptionStrategyRegistry
                        .get(strategyId)
                        .apply(keyAlias);

                existingStrategy.generateKeyIfNeeded();
                SecretsManager secretsManager = SecretsManager.getInstance(existingStrategy);

                long daysSince = (System.currentTimeMillis() - encryptionDate.getTime()) / (1000 * 60 * 60 * 24);

                if (daysSince >= ENCRYPTION_ROTATION_THRESHOLD_DAYS) {
                    String newStrategyId = EncryptionStrategyRegistry.getDefaultStrategyId();
                    String newKeyAlias = "key_" + newStrategyId + "_" + new Date(System.currentTimeMillis());
                    EncryptionStrategy newStrategy = encryptionStrategyRegistry
                            .get(newStrategyId)
                            .apply(newKeyAlias);

                    newStrategy.generateKeyIfNeeded();

                    encryptionService.reEncryptAllSecrets(newStrategy).join();
                    userRepository.setKeyAlias(newKeyAlias).join();
                    existingStrategy.deleteKey();
                    secretsManager.setStrategy(newStrategy);
                }
            } catch (Exception e) {
                throw new RuntimeException("Login failed", e);
            }
        });
    }
}