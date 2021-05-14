package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    TextView tv_nameRoom;
    TextView tv_valueGasConcentration;
    MQTTService mqttService;
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
                sendDataMQTT("1000", "biennguyenbk00/feeds/output.buzzer");
                sendDataMQTT("240", "biennguyenbk00/feeds/output.drv");
                sendDataMQTT("1", "biennguyenbk00/feeds/output.led");
                Intent intent = new Intent(WarningActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        loadValueInfo();



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
                Toast.makeText(getApplicationContext(), "Can not connect to server :(", Toast.LENGTH_LONG).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void loadValueInfo()
    {
        String value= getIntent().getStringExtra("value");
        String roomName= getIntent().getStringExtra("room_name");
        tv_nameRoom.setText(roomName);
        tv_valueGasConcentration.setText(value);
    }

    private void sendDataMQTT(String data, String topic) {
        MqttMessage msg = new MqttMessage();
        msg.setId(123456);
        msg.setQos(0);
        msg.setRetained(true);
        byte[] b = data.getBytes(Charset.forName("UTF−8"));
        msg.setPayload(b);
        Log.d("MQTT","Publish:"+ msg);
        try {
            mqttService.mqttAndroidClient.publish(topic, msg);
        } catch ( MqttException e) {
            Log.d("MQTT","sendDataMQTT: cannot send message");
        }
    }
}