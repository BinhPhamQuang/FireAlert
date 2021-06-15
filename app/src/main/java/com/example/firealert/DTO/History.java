package com.example.firealert.DTO;

public class History {
    private String date;
    private String id;
    private String name;
    private float value;

    public  History()
    {}
    public History(String date, String id, String name, float value) {
        this.date = date;
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String room_id) {
        this.id = room_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
