package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    View layoutAddroom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    layoutAddroom.setVisibility(View.VISIBLE);
            }
        });

        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnTips = (ImageButton) findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),TipsActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnRoomDetail = (ImageButton) findViewById(R.id.btnRoomDetail);
        btnRoomDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent= new Intent(getApplicationContext(),RoomDetailActivity.class);
//                startActivity(intent);
            }
        });
    }
}