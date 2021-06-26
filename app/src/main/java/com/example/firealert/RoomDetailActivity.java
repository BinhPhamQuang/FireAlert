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

import com.example.firealert.Adapter.RoomDetailAdapter;
import com.example.firealert.Service.BackgroundService;
import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


public class RoomDetailActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String,String>> list = new ArrayList<>();
    public static final String ROOM_NAME = "1";
    public static final String GAS ="2";
    public static RoomDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.clear();
        setContentView(R.layout.activity_room_detail);

        ImageButton btn_back= (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        ListView listView = (ListView) findViewById(R.id.listView);
        if(list.size()==0 || list ==null)
            list =  (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("listRoom");

        adapter = new RoomDetailAdapter(this, list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i< BackgroundService.mqttServiceGet.gasTopic.size(); i++)
                    Log.d("Rooms: ",BackgroundService.mqttServiceGet.gasTopic.get(i));
                Intent intent= new Intent(getApplicationContext(),HistoryActivity.class);

                intent.putExtra("user_1",getIntent().getStringExtra("user_1"));
                intent.putExtra("pass_1",getIntent().getStringExtra("pass_1"));
                intent.putExtra("user_2",getIntent().getStringExtra("user_2"));
                intent.putExtra("pass_2",getIntent().getStringExtra("pass_2"));
                //send room_id here
                intent.putExtra("room_id",Integer.toString(position));
                intent.putExtra("listRoom",list);
                intent.putExtra("room_name",BackgroundService.mqttServiceSend.rooms.get(position));
                intent.putExtra("room_topic", BackgroundService.mqttServiceGet.gasTopic.get(position));
                //-------------------------------
                startActivity(intent);

            }
        });

    }

}
