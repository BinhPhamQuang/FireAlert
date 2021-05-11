package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    TextView tv_nameRoom;
    TextView tv_valueGasConcentration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);


        btn_ignore= findViewById(R.id.btn_ignore);
        btn_fixitnow= findViewById(R.id.btn_fixitnow);
        tv_nameRoom= findViewById(R.id.tv_nameRoom);
        tv_valueGasConcentration= findViewById(R.id.tv_valueGasConcentration);



        btn_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btn_fixitnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        loadValueInfo();
    }

    private void loadValueInfo()
    {
        String value= getIntent().getStringExtra("value");
        String roomName= getIntent().getStringExtra("room_name");
        tv_nameRoom.setText(roomName);
        tv_valueGasConcentration.setText(value);
    }
}