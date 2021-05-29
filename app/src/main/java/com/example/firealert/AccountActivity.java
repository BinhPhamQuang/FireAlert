package com.example.firealert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class AccountActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    TabLayout tab_layout;
    MQTTService mqttService;
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
                        Intent intent = new Intent(AccountActivity.this, WarningActivity.class);
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