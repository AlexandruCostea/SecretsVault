package com.alexcostea.secretsvault.repository.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexcostea.secretsvault.database.connection.MobileDatabaseConnectionManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserDataRepository implements UserRepository {

    @Override
    public CompletableFuture<Optional<String>> getKeyAlias() throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            Optional<String> keyAlias = Optional.empty();
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();
                String sql = "SELECT key_alias FROM User WHERE id = 1";

                Cursor cursor = database.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    String alias = cursor.getString(cursor.getColumnIndexOrThrow("key_alias"));
                    keyAlias = Optional.ofNullable(alias);
                }
                cursor.close();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching key alias: " + e.getMessage(), e);
            }
            return keyAlias;
        });
    }

    @Override
    public CompletableFuture<Void> setKeyAlias(String keyAlias) throws RuntimeException {
        return CompletableFuture.runAsync(() -> {
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();
                String sql = """
                        REPLACE INTO User (id, key_alias)
                        VALUES (1, ?)
                        """;
                String[] selectionArgs = {keyAlias};
                database.execSQL(sql, selectionArgs);
            } catch (Exception e) {
                throw new RuntimeException("Error setting key alias: " + e.getMessage(), e);
            }
        });
    }
}