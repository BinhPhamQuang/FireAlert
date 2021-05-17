package com.example.firealert;

import android.content.Intent;
import android.os.Bundle;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.example.firealert.FragmentTips1;
import com.example.firealert.Service.MQTTService;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TipsActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    DotsIndicator dotsIndicator;
    ImageButton btnBack;
    ImageButton btnNext;
    MQTTService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager= findViewById(R.id.viewpager_tips);
        btnBack= findViewById(R.id.btnBack);
        btnNext= findViewById(R.id.btnNext);
        dotsIndicator = findViewById(R.id.dots_indicator);

        getTab();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (viewPager.getCurrentItem()==0)
//                {
//                    startActivity(new Intent(TipsActivity.this,HomeActivity.class));
//                    finish();
//                }
                if (viewPager.getCurrentItem()!=0)
                {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=7)
                {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
//                else
//                {
//                    startActivity(new Intent(TipsActivity.this,HomeActivity.class));
//                    finish();
//                }
            }
        });


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
                Toast.makeText(getApplicationContext(), "Can not connect to server :(", Toast.LENGTH_LONG).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (message.toString().equals("0") == false) {
                    Intent intent = new Intent(TipsActivity.this, WarningActivity.class);
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
    private void getTab()
    {
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPageAdapter.addFragment(FragmentTips1.getInstance(),"1");
                viewPageAdapter.addFragment(FragmentTips2.getInstance(),"2");
                viewPageAdapter.addFragment(FragmentTips3.getInstance(),"3");
                viewPageAdapter.addFragment(FragmentTips4.getInstance(),"4");
                viewPageAdapter.addFragment(FragmentTips5.getInstance(),"5");
                viewPageAdapter.addFragment(FragmentTips6.getInstance(),"6");
                viewPageAdapter.addFragment(FragmentTips7.getInstance(),"7");
                viewPageAdapter.addFragment(FragmentTips8.getInstance(),"8");
                viewPager.setAdapter(viewPageAdapter);
                dotsIndicator.setViewPager(viewPager);
            }
        });
    }
}