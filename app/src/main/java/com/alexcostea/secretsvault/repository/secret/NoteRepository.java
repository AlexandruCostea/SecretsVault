package com.alexcostea.secretsvault.repository.secret;

import android.content.ContentValues;
import android.database.Cursor;

import com.alexcostea.secretsvault.domain.entities.Note;
import com.alexcostea.secretsvault.domain.entities.Secret;
import com.alexcostea.secretsvault.domain.factories.SecretFactory;

import java.util.List;

public class NoteRepository extends AbstractSecretRepository<Note> {

    public NoteRepository(SecretFactory previewFactory, SecretFactory noteFactory) {
        super(previewFactory, noteFactory);
    }

    @Override
    protected Note cast(Secret secret) {
        return (Note) secret;
    }

    @Override
    protected Secret parseFullSecret(Cursor cursor) throws IllegalArgumentException {
        return fullFactory.createSecret(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("updated_at")),
                cursor.getString(cursor.getColumnIndexOrThrow("content"))
        );
    }

    @Override
    protected String getTableName() {
        return "Note";
    }

    @Override
    protected String getSelectFullSQL() {
        return """
                SELECT id, title, updated_at, content
                FROM Note
                """;
    }

    @Override
    protected String getSelectFullByIdSQL() {
        return """
                SELECT id, title, updated_at, content
                FROM Note
                WHERE id = ?;
                """;
    }

    @Override
    protected void prepareLargeInsertParameters(ContentValues contentValues, Note entity) {
        contentValues.put("title", entity.getTitle());
        contentValues.put("content", entity.getContent());
    }

    @Override
    protected void prepareUpdateContentValues(ContentValues contentValues, Note entity) {
        contentValues.put("title", entity.getTitle());
        contentValues.put("updated_at", entity.getUpdatedAt());
        contentValues.put("content", entity.getContent());
    }

    @Override
    protected String getInsertStatementPrefix() {
        return "INSERT INTO Note (title, updated_at, content) VALUES";
    }

    @Override
    protected String getInsertValuePlaceholders() {
        return "  (?, ?, ?)";
    }

    @Override
    protected void prepareLargeInsertParameters(List<String> insertParams, Note secret) {
        insertParams.add(secret.getTitle());
        insertParams.add(secret.getUpdatedAt());
        insertParams.add(secret.getContent());
    }
}