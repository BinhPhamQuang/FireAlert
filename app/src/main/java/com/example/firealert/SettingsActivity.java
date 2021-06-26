package com.example.firealert;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.DTO.Room;
import com.example.firealert.DTO.UserData;
import com.example.firealert.Service.BackgroundService;
import com.example.firealert.Service.MQTTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Hashtable;


public class SettingsActivity extends AppCompatActivity {
    TextView helloTextView;
    private String userId, email, address, phone, username, houseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        helloTextView = (TextView) findViewById(R.id.txtview_hello);

        userId = getIntent().getStringExtra("userId");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        houseId = getIntent().getStringExtra("houseId");
        helloTextView.setText(String.format("Hello %s!", username));

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout rl_account = (RelativeLayout) findViewById(R.id.rl_account);
        rl_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),AccountActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("username", username);
                intent.putExtra("houseId", houseId);

                intent.putExtra("user_1", getIntent().getStringExtra("user_1"));
                intent.putExtra("pass_1", getIntent().getStringExtra("pass_1"));
                intent.putExtra("user_2", getIntent().getStringExtra("user_2"));
                intent.putExtra("pass_2", getIntent().getStringExtra("pass_2"));
                startActivity(intent);
            }
        });

        RelativeLayout rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),AboutActivity.class);

                intent.putExtra("user_1", getIntent().getStringExtra("user_1"));
                intent.putExtra("pass_1", getIntent().getStringExtra("pass_1"));
                intent.putExtra("user_2", getIntent().getStringExtra("user_2"));
                intent.putExtra("pass_2", getIntent().getStringExtra("pass_2"));
                startActivity(intent);
            }
        });

        RelativeLayout rl_privacy = (RelativeLayout) findViewById(R.id.rl_privacy);
        rl_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), PrivacyActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("username", username);
                intent.putExtra("houseId", houseId);

                intent.putExtra("user_1", getIntent().getStringExtra("user_1"));
                intent.putExtra("pass_1", getIntent().getStringExtra("pass_1"));
                intent.putExtra("user_2", getIntent().getStringExtra("user_2"));
                intent.putExtra("pass_2", getIntent().getStringExtra("pass_2"));
                startActivity(intent);
            }
        });

        Button btnSignOut = findViewById(R.id.btn_signOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundService.user_1 = "";
                BackgroundService.user_2 = "";
                BackgroundService.pass_1 = "";
                BackgroundService.pass_2 = "";
                MainActivity.firstAddGet = true;
                MainActivity.firstAddSend = true;
                BackgroundService.mqttServiceGet = null;
                BackgroundService.mqttServiceSend = null;
                BackgroundService.haveRead = false;
                HomeActivity.list = new ArrayList<>();
                HomeActivity.adapter = null;
                RoomDetailActivity.list = new ArrayList<>();
                RoomDetailActivity.adapter = null;
                HomeActivity.firstAdd = true;

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                
                startActivity(intent);
            }
        });


    }
}