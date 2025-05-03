package com.alexcostea.secretsvault.repository.secret;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexcostea.secretsvault.database.connection.MobileDatabaseConnectionManager;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.domain.factories.SecretFactory;
import com.alexcostea.secretsvault.utils.sql.FullUpdateQuery;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractSecretRepository<T extends Secret> implements SecretRepository {

    protected final SecretFactory previewFactory;
    protected final SecretFactory fullFactory;


    protected AbstractSecretRepository(SecretFactory previewFactory, SecretFactory fullFactory) {
        this.previewFactory = previewFactory;
        this.fullFactory = fullFactory;

    }

    protected abstract T cast(Secret secret);
    protected abstract Secret parseFullSecret(Cursor cursor) throws IllegalArgumentException;
    protected abstract String getTableName();
    protected abstract String getSelectFullSQL();
    protected abstract String getSelectFullByIdSQL();
    protected abstract void prepareLargeInsertParameters(ContentValues contentValues, T secret);
    protected abstract void prepareUpdateContentValues(ContentValues contentValues, T secret);
    protected abstract String getInsertStatementPrefix();
    protected abstract String getInsertValuePlaceholders();
    protected abstract void prepareLargeInsertParameters(List<String> insertParams, T secret);

    @Override
    public CompletableFuture<List<Secret>> getSecretPreviews() throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            List<Secret> secrets = new ArrayList<>();
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();
                String query = """
                        SELECT id, title, updated_at
                        FROM""" + " " + getTableName() + " " +"""
                        ORDER BY updated_at DESC;
                        """;

                Cursor cursor = database.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"));

                        Secret secret = previewFactory.createSecret(id, title, updatedAt);
                        secrets.add(secret);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return secrets;
            } catch (Exception e) {
                throw new RuntimeException("Error fetching all secrets: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<Secret>> getFullSecrets() throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            List<Secret> secrets = new ArrayList<>();
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();
                String query = getSelectFullSQL();
                Cursor cursor = database.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        Secret secret = parseFullSecret(cursor);
                        secrets.add(secret);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return secrets;
            } catch (Exception e) {
                throw new RuntimeException("Error fetching all secrets: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Secret> getFullSecret(Integer id) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();

                String query = getSelectFullByIdSQL();
                String[] selectionArgs = {String.valueOf(id)};

                Cursor cursor = database.rawQuery(query, selectionArgs);
                if (cursor.moveToFirst()) {
                    Secret secret = parseFullSecret(cursor);
                    cursor.close();
                    return secret;
                }
                cursor.close();
                throw new Exception("Secret with id " + id + " not found");
            } catch (Exception e) {
                throw new RuntimeException("Error fetching note: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Secret> addSecret(Secret secret) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();

                T entity = cast(secret);
                ContentValues values = new ContentValues();
                prepareLargeInsertParameters(values, entity);

                long id = database.insert(getTableName(), null, values);
                if (id != -1) {
                    String updatedAt = new Date(System.currentTimeMillis()).toString();
                    return previewFactory.createSecret(
                            (int) id,
                            entity.getTitle(),
                            updatedAt
                    );
                }
                throw new Exception("Failed to insert secret to database");
            } catch (Exception e) {
                throw new RuntimeException("Error adding secret: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Secret> updateSecret(Secret secret) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();

                T entity = cast(secret);
                entity.setUpdatedAt(new Date(System.currentTimeMillis()).toString());

                ContentValues values = new ContentValues();
                prepareUpdateContentValues(values, entity);

                String whereClause = "id = ?";
                String[] whereArgs = {String.valueOf(entity.getId())};

                int updated = database.update(getTableName(), values, whereClause, whereArgs);
                if (updated != 0) {
                    return previewFactory.createSecret(
                            entity.getId(),
                            entity.getTitle(),
                            entity.getUpdatedAt()
                    );
                }
                throw new Exception("Failed to update secret in database");
            } catch (Exception e) {
                throw new RuntimeException("Error updating secret: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteSecret(Integer id) throws RuntimeException {
        return CompletableFuture.runAsync(() -> {
            try {
                SQLiteDatabase database = MobileDatabaseConnectionManager.getDatabase();

                String whereClause = "id = ?";
                String[] whereArgs = {String.valueOf(id)};

                int deleted = database.delete(getTableName(), whereClause, whereArgs);
                if (deleted == 0) {
                    throw new Exception("Failed to delete secret from database");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error deleting secret: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public FullUpdateQuery getFullUpdateSQL(List<Secret> secrets) throws RuntimeException {
        String deleteQuery = "DELETE FROM " + getTableName() + ";";

        List<String> insertParams = new ArrayList<>();
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append(this.getInsertStatementPrefix());

        for (int i = 0; i < secrets.size(); i++) {
            T entity = cast(secrets.get(i));
            this.prepareLargeInsertParameters(insertParams, entity);
            insertQuery.append(this.getInsertValuePlaceholders());
            if (i < secrets.size() - 1) {
                insertQuery.append(",");
            }
        }
        insertQuery.append(";");

        return new FullUpdateQuery(
                deleteQuery,
                insertQuery.toString(),
                insertParams
        );
    }
}