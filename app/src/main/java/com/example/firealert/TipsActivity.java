package com.example.firealert;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.firealert.Adapter.ViewPageAdapter;
import com.example.firealert.fragment_tips.FragmentTips1;
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_tips.FragmentTips2;
import com.example.firealert.fragment_tips.FragmentTips3;
import com.example.firealert.fragment_tips.FragmentTips4;
import com.example.firealert.fragment_tips.FragmentTips5;
import com.example.firealert.fragment_tips.FragmentTips6;
import com.example.firealert.fragment_tips.FragmentTips7;
import com.example.firealert.fragment_tips.FragmentTips8;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Hashtable;

public class TipsActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageButton btn_back;
    ViewPageAdapter viewPageAdapter;
    DotsIndicator dotsIndicator;
    ImageButton btnBack;
    ImageButton btnNext;
    MQTTService mqttServiceGet;
    MQTTService mqttServiceSend;

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
                if (Float.parseFloat(mess.get("data")) == 1)
                {
                    Log.d("Message Arrived: ", topic);
                    String GasConcentration = mess.get("data");
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(TipsActivity.this, WarningActivity.class);
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