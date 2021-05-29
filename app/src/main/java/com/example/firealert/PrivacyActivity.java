package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PrivacyActivity extends AppCompatActivity {
    MQTTService mqttService;
    String username;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.txt_view);

        username = getIntent().getStringExtra("username");
        textView.setText("Hello " + username + "!");

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btn_saveChange = (Button) findViewById(R.id.btn_saveChange);
        btn_saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SettingsActivity.class);
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
                if (Float.parseFloat(message.toString()) >= 10) {
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
//                        mqttService.sendDataMQTT("1000", "biennguyenbk00/feeds/output.buzzer");
//                        mqttService.sendDataMQTT("1", "biennguyenbk00/feeds/output.led");
                        mqttService.sendDataMQTT(mqttService.SPEAKER, "biennguyenbk00/feeds/output.buzzer");
                        mqttService.sendDataMQTT(mqttService.LED, "biennguyenbk00/feeds/output.led");
                        Intent intent = new Intent(PrivacyActivity.this, WarningActivity.class);
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