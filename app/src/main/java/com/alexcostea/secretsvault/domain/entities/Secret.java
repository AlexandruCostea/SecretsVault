package com.alexcostea.secretsvault.domain.entities;

public interface Secret {
    Integer getId();
    void setId(Integer id);
    String getTitle();
    void setTitle(String title);
    String getUpdatedAt();
    void setUpdatedAt(String updatedAt);
}