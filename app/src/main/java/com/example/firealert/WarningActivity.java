package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);


        btn_ignore= findViewById(R.id.btn_ignore);
        btn_fixitnow= findViewById(R.id.btn_fixitnow);

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
    }
}