package com.alexcostea.secretsvault.database.schema.table;

public class UserTable implements Table {
    public String getQuery() {
        return """
                CREATE TABLE IF NOT EXISTS User (
                    id INTEGER PRIMARY KEY DEFAULT 1,
                    key_alias TEXT NOT NULL
                );
                """;
    }
}
