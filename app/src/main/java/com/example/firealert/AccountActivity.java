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

    String userId, email, address, phone, username, houseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager_account);
        tab_layout= findViewById(R.id.tab_layout);

        userId = getIntent().getStringExtra("userId");
        email = getIntent().getStringExtra("email");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        username = getIntent().getStringExtra("username");
        houseId = getIntent().getStringExtra("houseId");

        getTab();

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    private void getTab()
    {
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.addFragment(FragmentAccountTab1.getInstance(userId, email, username, phone, address, houseId),"Information");
                viewPageAdapter.addFragment(FragmentAccountTab2.getInstance(houseId),"Account");
                viewPager.setAdapter(viewPageAdapter);
                tab_layout.setupWithViewPager(viewPager);
            }
        });
    }
}