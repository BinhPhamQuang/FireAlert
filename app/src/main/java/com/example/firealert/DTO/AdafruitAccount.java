package com.example.firealert.DTO;

public class AdafruitAccount {
    private String username;
    private String password;

    public AdafruitAccount() {
    }

    public AdafruitAccount(String password, String username) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
