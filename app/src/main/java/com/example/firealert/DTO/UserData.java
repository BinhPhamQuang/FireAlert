package com.example.firealert.DTO;

public class UserData {
    private String user_id;
    private String email;
    private String username;
    private String address;
    private String phone;

    public UserData() {

    }

    public UserData(String user_id, String email, String username, String address, String phone) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.address = address;
        this.phone = phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
