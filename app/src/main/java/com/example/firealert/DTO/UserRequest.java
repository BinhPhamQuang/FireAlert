package com.example.firealert.DTO;

public class UserRequest {
    private String username;
    private String key;

    public UserRequest(String username, String key) {
        this.username = username;
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
