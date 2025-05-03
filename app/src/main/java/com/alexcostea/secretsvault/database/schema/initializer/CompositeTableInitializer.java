package com.alexcostea.secretsvault.database.schema.initializer;

import android.database.sqlite.SQLiteException;

import java.util.List;

public class CompositeTableInitializer implements TableInitializer {
    private final List<TableInitializer> children;

    public CompositeTableInitializer(List<TableInitializer> children) {
        this.children = children;
    }

    public void add(TableInitializer child) {
        children.add(child);
    }

    public void remove(TableInitializer child) {
        children.remove(child);
    }

    @Override
    public void initialize() throws SQLiteException {
        for (TableInitializer child : children) {
            child.initialize();
        }
    }
}