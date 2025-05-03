package com.alexcostea.secretsvault.database.schema.table;

public class NoteTable implements Table {
    public String getQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Note (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    updated_at TEXT DEFAULT (DATE('now')),
                    content TEXT DEFAULT NULL
                );
                """;
    }
}
