package com.alexcostea.secretsvault.service.encryption;

import android.database.sqlite.SQLiteDatabase;

import com.alexcostea.secretsvault.database.connection.MobileDatabaseConnectionManager;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.service.secrets.SecretService;
import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;
import com.alexcostea.secretsvault.utils.sql.FullUpdateQuery;

import android.database.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SecretsEncryptionService implements EncryptionService {

    private final List<SecretService> services;

    public SecretsEncryptionService(List<SecretService> services) {
        this.services = services;
    }

    @Override
    public CompletableFuture<Void> reEncryptAllSecrets(EncryptionStrategy newStrategy) throws RuntimeException {
        return CompletableFuture.runAsync(() -> {
            try {
                List<FullUpdateQuery> queries = new ArrayList<>();
                for (SecretService service : services) {
                    List<Secret> secrets = service.getFullSecrets().join();
                    List<Secret> reEncryptedSecrets = new ArrayList<>();
                    for (Secret secret : secrets) {
                        secret = service.reEncryptSecret(secret, newStrategy);
                        reEncryptedSecrets.add(secret);
                    }
                    FullUpdateQuery query = service.getBatchUpdateSQL(reEncryptedSecrets);
                    queries.add(query);
                }

                try {
                    SQLiteDatabase db = MobileDatabaseConnectionManager.getDatabase();
                    db.beginTransaction();
                    for (FullUpdateQuery query : queries) {
                        db.execSQL(query.deleteQuery());

                        String insertQuery = query.insertQuery();
                        String[] insertArgs = query.insertParameters()
                                .toArray(new String[0]);
                        db.execSQL(insertQuery, insertArgs);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();

                } catch (SQLException e) {
                    throw new RuntimeException("Error executing full re-encryption", e);
                }
            } catch (Exception e) {
                throw new RuntimeException("Secrets manager not initialized.", e);
            }
        });
    }
}
