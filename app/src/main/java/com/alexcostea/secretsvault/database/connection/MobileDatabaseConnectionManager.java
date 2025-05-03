package com.alexcostea.secretsvault.database.connection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class MobileDatabaseConnectionManager {

    private static SQLiteDatabase database;

    public static void initialize(Context context) throws SQLiteException {
        if (database == null) {
            database = context.openOrCreateDatabase("secretsvault.db", Context.MODE_PRIVATE, null);
        }
    }

    public static SQLiteDatabase getDatabase() throws IllegalStateException{
        if (database == null) {
            throw new IllegalStateException("DatabaseConnectionManager not initialized!");
        }
        return database;
    }
}
