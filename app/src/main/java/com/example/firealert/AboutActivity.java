package com.example.firealert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.firealert.DTO.AdafruitAccount;
import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class AboutActivity extends AppCompatActivity {


    private ArrayList<HashMap<String,String>> list = new ArrayList<>();
    private ArrayList<AdafruitAccount> listAccount = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        try {
//            mqttServiceGet = new MQTTService(this, getIntent().getStringExtra("user_1"),getIntent().getStringExtra("pass_1"),"123456",false);
//            mqttServiceSend = new MQTTService(this, getIntent().getStringExtra("user_2"), getIntent().getStringExtra("pass_2"),"654321",true);
//        }
//        catch (MqttException e) {
//            e.printStackTrace();
//        }
//
//        mqttServiceGet.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server get", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                Hashtable<String,String> mess = mqttServiceGet.getMessage(message.toString());
//
//                System.out.println(mess);
//                String data = mess.get("data");
//
//
//                int indexTopic = 0;
//                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
//                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
//                        indexTopic = i;
//                    }
//                }
//                if (data.equals("1")) {
//                    Log.d("Message Arrived: ", topic);
//                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
//                        Intent intent = new Intent(AboutActivity.this, WarningActivity.class);
//                        // change this value for send data to another activity
//                        intent.putExtra("room_name", mqttServiceGet.rooms.get(indexTopic));
//                        //--------------------------------------------
//                        intent.putExtra("value", mess.get("data"));
//                        // must change this value by room_id
//                        intent.putExtra("indexTopic",indexTopic);
//                        startActivity(intent);
//                    }
//                }
//                else {
//                    HomeActivity.badge.setVisibility(View.INVISIBLE);
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER_OFF, mqttServiceSend.buzzerTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.LED_OFF, mqttServiceSend.ledTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM_OFF,mqttServiceSend.drvTopic.get(indexTopic));
//                }
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//        mqttServiceSend.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server send", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//            }
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
    }
}