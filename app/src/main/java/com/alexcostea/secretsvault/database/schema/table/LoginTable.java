package com.alexcostea.secretsvault.database.schema.table;

public class LoginTable implements Table {
    public String getQuery() {
        return """
                CREATE TABLE IF NOT EXISTS Login (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    updated_at TEXT DEFAULT (DATE('now')),
                    mail_or_username TEXT DEFAULT NULL,
                    password TEXT DEFAULT NULL
                );
                """;
    }
}
