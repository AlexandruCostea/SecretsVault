package com.alexcostea.secretsvault.domain.entities;

import java.sql.Date;

public class SecretSummary implements Secret {

    private Integer id;
    private String title;
    private String updatedAt;

    public SecretSummary(Integer id, String title, String updatedAt) {
        this.id = id;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public SecretSummary(String title) {
        this(0, title, new Date(System.currentTimeMillis()).toString());
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}