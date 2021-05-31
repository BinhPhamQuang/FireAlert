package com.example.firealert;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class SettingsActivity extends AppCompatActivity {
    TextView helloTextView;
    MQTTService mqttService;
    String userId;
    String email, address, phone, username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        helloTextView = (TextView) findViewById(R.id.txtview_hello);

        username = getIntent().getStringExtra("username");
        userId = getIntent().getStringExtra("user_id");
        email = getIntent().getStringExtra("email");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
        helloTextView.setText("Hello " + username + "!");

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
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        RelativeLayout rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout rl_privacy = (RelativeLayout) findViewById(R.id.rl_privacy);
        rl_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),PrivacyActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button btn_signOut = (Button) findViewById(R.id.btn_signOut);
        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


        try {
            mqttService = new MQTTService(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if ( Float.parseFloat(message.toString()) >= 10)
                {
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
//                        mqttService.sendDataMQTT("1000", "biennguyenbk00/feeds/output.buzzer");
//                        mqttService.sendDataMQTT("1", "biennguyenbk00/feeds/output.led");
                        mqttService.sendDataMQTT(mqttService.SPEAKER, "biennguyenbk00/feeds/output.buzzer");
                        mqttService.sendDataMQTT(mqttService.LED, "biennguyenbk00/feeds/output.led");
                        Intent intent = new Intent(SettingsActivity.this, WarningActivity.class);
                        // change this value for send data to another activity
                        intent.putExtra("room_name", "Room 1");
                        //--------------------------------------------
                        intent.putExtra("value", message.toString());
                        startActivity(intent);
                    }
                }
                else {
                    HomeActivity.badge.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}