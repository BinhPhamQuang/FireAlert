package com.example.firealert.DTO;

public class User {

    private  User()
    {

    }
    public static User getInstance()
    {
        User user = new User();
        return  user;
    }
    //set default value for test mode !
    private String user_id = "1";
    private int house_id = 1;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setHouse_id(int house_id) {
        this.house_id = house_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getHouse_id() {
        return house_id;
    }
}
