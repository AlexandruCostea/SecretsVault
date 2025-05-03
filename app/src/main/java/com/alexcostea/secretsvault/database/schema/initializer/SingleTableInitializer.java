package com.alexcostea.secretsvault.database.schema.initializer;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.alexcostea.secretsvault.database.connection.MobileDatabaseConnectionManager;

public class SingleTableInitializer implements TableInitializer {
    private final String tableQuery;

    public SingleTableInitializer(String tableQuery) {
        this.tableQuery = tableQuery;
    }

    @Override
    public void initialize() throws SQLiteException {
        SQLiteDatabase db = MobileDatabaseConnectionManager.getDatabase();
        db.execSQL(this.tableQuery);
    }
}