package com.alexcostea.secretsvault.database.schema.initializer;

import android.database.sqlite.SQLiteException;

public interface TableInitializer {
    void initialize() throws SQLiteException;
}
