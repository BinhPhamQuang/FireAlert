package com.example.firealert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.example.firealert.API.ApiService;
import com.example.firealert.Adapter.HistoryDataAdapter;
import com.example.firealert.Adapter.RoomDetailAdapter;
import com.example.firealert.DTO.History;
import com.example.firealert.DTO.UserRequest;
import com.example.firealert.Service.MQTTService;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RoomDetailActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String GAS ="2";
    MQTTService mqttServiceGet;
    MQTTService mqttServiceSend;

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

//        populateList();
        Intent getIntent = getIntent();
        list =  (ArrayList<HashMap<String,String>>) getIntent.getSerializableExtra("listRoom");
        ListView listView = (ListView) findViewById(R.id.listView);
        RoomDetailAdapter adapter = new RoomDetailAdapter(this, list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(RoomDetailActivity.this,HistoryActivity.class);

                //send room_id here
                intent.putExtra("room_id","0");
                //-------------------------------
                startActivity(intent);


            }
        });


        try {
            mqttServiceGet = new MQTTService(this,MainActivity.Server_username_get,MainActivity.Server_password_get,"123456",false);
            mqttServiceSend = new MQTTService(this,MainActivity.Server_username_send,MainActivity.Server_password_send,"654321",true);
            //AddItemsToRecyclerViewArrayList();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
        mqttServiceGet.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(),"Can't connect to server get", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Hashtable<String,String> mess = mqttServiceGet.getMessage(message.toString());
                int indexTopic = 0;
                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
                        indexTopic = i;
                    }
                }

                list.get(indexTopic).put("2",mess.get("data"));
                adapter.notifyDataSetChanged();
                if (Float.parseFloat(mess.get("data")) == 1)
                {
                    Log.d("Message Arrived: ", topic);
                    String GasConcentration = mess.get("data");
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(RoomDetailActivity.this, WarningActivity.class);
                        // change this value for send data to another activity
                        intent.putExtra("room_name", mqttServiceSend.rooms.get(indexTopic));
                        //--------------------------------------------
                        intent.putExtra("value", mess.get("data"));
                        // must change this value by room_id
                        intent.putExtra("indexTopic",indexTopic);
                        startActivity(intent);
                    }
                }
                else {
                    HomeActivity.badge.setVisibility(View.INVISIBLE);
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER_OFF, mqttServiceSend.buzzerTopic.get(indexTopic));
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.LED_OFF, mqttServiceSend.ledTopic.get(indexTopic));
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM_OFF,mqttServiceSend.drvTopic.get(indexTopic));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        mqttServiceSend.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(),"Can't connect to server send", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

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
