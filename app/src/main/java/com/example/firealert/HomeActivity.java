package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    View layoutAddroom;
    LinearLayout ll_close_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        ll_close_layout =findViewById(R.id.ll_close_layout);
        ll_close_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddroom.setVisibility(View.INVISIBLE);
            }
        });


        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        String[] splitDate = formattedDate.split(",");

        TextView tv_currentDate = (TextView) findViewById(R.id.tv_currentDate);
        tv_currentDate.setText(splitDate[1]  + ", " + splitDate[2]);


        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBottomSheet bottomsheet = new FragmentBottomSheet();
                bottomsheet.show(getSupportFragmentManager(),"add_room");
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
                Intent intent= new Intent(getApplicationContext(),RoomDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}