package com.alexcostea.secretsvault.repository.secret;

import android.content.ContentValues;
import android.database.Cursor;

import com.alexcostea.secretsvault.domain.entities.Login;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.domain.factories.SecretFactory;

import java.util.List;

public class LoginRepository extends AbstractSecretRepository<Login> {

    public LoginRepository(SecretFactory previewFactory, SecretFactory loginFactory) {
        super(previewFactory, loginFactory);
    }

    @Override
    protected Login cast(Secret secret) {
        return (Login) secret;
    }

    @Override
    protected Secret parseFullSecret(Cursor cursor) throws IllegalArgumentException {
        return fullFactory.createSecret(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("updated_at")),
                cursor.getString(cursor.getColumnIndexOrThrow("mail_or_username")),
                cursor.getString(cursor.getColumnIndexOrThrow("password"))
        );
    }

    @Override
    protected String getTableName() {
        return "Login";
    }

    @Override
    protected String getSelectFullSQL() {
        return """
                SELECT id, title, updated_at, mail_or_username, password
                FROM Login
                """;
    }

    @Override
    protected String getSelectFullByIdSQL() {
        return """
                SELECT id, title, updated_at, mail_or_username, password
                FROM Login
                WHERE id = ?;
                """;
    }

    @Override
    protected void prepareLargeInsertParameters(ContentValues contentValues, Login entity) {
        contentValues.put("title", entity.getTitle());
        contentValues.put("mail_or_username", entity.getMailOrUsername());
        contentValues.put("password", entity.getPassword());
    }

    @Override
    protected void prepareUpdateContentValues(ContentValues contentValues, Login entity) {
        contentValues.put("title", entity.getTitle());
        contentValues.put("updated_at", entity.getUpdatedAt());
        contentValues.put("mail_or_username", entity.getMailOrUsername());
        contentValues.put("password", entity.getPassword());
    }

    @Override
    protected String getInsertStatementPrefix() {
        return "INSERT INTO Login (title, updated_at, mail_or_username, password) VALUES";
    }

    @Override
    protected String getInsertValuePlaceholders() {
        return " (?, ?, ?, ?)";
    }

    @Override
    protected void prepareLargeInsertParameters(List<String> insertParams, Login secret) {
        insertParams.add(secret.getTitle());
        insertParams.add(secret.getUpdatedAt());
        insertParams.add(secret.getMailOrUsername());
        insertParams.add(secret.getPassword());
    }
}