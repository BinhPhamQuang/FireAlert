package com.example.firealert.DTO;

public class History {
    private String date;
    private String house_id;
    private String room_id;
    private float value;

    public  History()
    {}
    public History(String date, String house_id, String room_id, float value) {
        this.date = date;
        this.house_id = house_id;
        this.room_id = room_id;
        this.value = value;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHouse_id(String house_id) {
        this.house_id = house_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getHouse_id() {
        return house_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public float getValue() {
        return value;
    }
}
