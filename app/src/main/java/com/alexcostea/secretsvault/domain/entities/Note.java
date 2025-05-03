package com.alexcostea.secretsvault.domain.entities;

public class Note extends SecretSummary {
    private String content;

    public Note(Integer id, String title, String updatedAt, String content) {
        super(id, title, updatedAt);

        this.content = content;
    }

    public Note(String title, String content) {
        super(title);

        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}