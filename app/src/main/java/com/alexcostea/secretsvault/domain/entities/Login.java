package com.alexcostea.secretsvault.domain.entities;

import org.jetbrains.annotations.NotNull;

public class Login extends SecretSummary {
    private String mailOrUsername;
    private String password;


    public Login(Integer id, String title, String updatedAt, String mailOrUsername, String password) {
        super(id, title, updatedAt);

        this.mailOrUsername = mailOrUsername;
        this.password = password;
    }

    public Login(String title, String mailOrUsername, String password) {
        super(title);
        this.mailOrUsername = mailOrUsername;
        this.password = password;
    }

    public String getMailOrUsername() {
        return mailOrUsername;
    }

    public void setMailOrUsername(String mailOrUsername) {
        this.mailOrUsername = mailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}