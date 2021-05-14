package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.example.firealert.Adapter.RoomDetailAdapter;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;


public class RoomDetailActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String GAS ="2";
    MQTTService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        ImageButton btn_back= (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        populateList();

        ListView listView = (ListView) findViewById(R.id.listView);
        RoomDetailAdapter adapter = new RoomDetailAdapter(this, list);
        listView.setAdapter(adapter);


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
                if (message.toString().equals("0") == false) {
                    Intent intent = new Intent(RoomDetailActivity.this, WarningActivity.class);
                    // change this value for send data to another activity
                    intent.putExtra("room_name", "Room 1");
                    //--------------------------------------------
                    intent.putExtra("value", message.toString());
                    startActivity(intent);

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    private void populateList() {
        list = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> hashmap2 = new HashMap<String,String>();
        hashmap2.put(ROOM_NAME, "Room 1");
        hashmap2.put(GAS, "0.00");
        list.add(hashmap2);

        HashMap<String,String> hashmap = new HashMap<String,String>();
        hashmap.put(ROOM_NAME, "Room 2");
        hashmap.put(GAS, "1.00");
        list.add(hashmap);

        HashMap<String,String> hashmap1 = new HashMap<String,String>();
        hashmap1.put(ROOM_NAME, "Room 3");
        hashmap1.put(GAS, "9.20");
        list.add(hashmap1);

    }
}
