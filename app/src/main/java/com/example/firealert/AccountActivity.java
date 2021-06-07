package com.example.firealert;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_account.FragmentAccountTab1;
import com.example.firealert.fragment_account.FragmentAccountTab2;
import com.google.android.material.tabs.TabLayout;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Hashtable;

public class AccountActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    TabLayout tab_layout;
    MQTTService mqttServiceGet;
    MQTTService mqttServiceSend;
    String address, phone, username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager_account);
        tab_layout= findViewById(R.id.tab_layout);

        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        username = getIntent().getStringExtra("username");

        getTab();

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            mqttServiceGet = new MQTTService(this,MainActivity.Server_username_get,MainActivity.Server_password_get);
            mqttServiceSend = new MQTTService(this,MainActivity.Server_username_send,MainActivity.Server_password_send);
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
                if (Float.parseFloat(mess.get("data")) == 1)
                {
                    Log.d("Message Arrived: ", topic);
                    String GasConcentration = mess.get("data");
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(AccountActivity.this, WarningActivity.class);
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
    private void getTab()
    {
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.addFragment(FragmentAccountTab1.getInstance(username, phone, address),"Information");
                viewPageAdapter.addFragment(FragmentAccountTab2.getInstance(),"Account");
                viewPager.setAdapter(viewPageAdapter);
                tab_layout.setupWithViewPager(viewPager);
            }
        });
    }
}