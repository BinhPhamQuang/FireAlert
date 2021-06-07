package com.example.firealert.DTO;

public class Room {
    public String buzzer,drv,gas,led,name;
    public Room(){

    }
    public Room(String buzzer, String drv, String gas,String led, String name) {
        this.buzzer = buzzer;
        this.drv = drv;
        this.gas = gas;
        this.led = led;
        this.name = name;
    }
}
