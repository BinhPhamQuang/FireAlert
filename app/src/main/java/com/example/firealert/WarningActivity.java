package com.example.firealert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.DAO.FireBaseHelper;
import com.example.firealert.DTO.AdafruitAccount;
import com.example.firealert.Service.BackgroundService;
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_bottom_sheet.Confirm;
import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    TextView tv_nameRoom;
    TextView tv_valueGasConcentration;


    int indexTopic;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        btn_ignore= findViewById(R.id.btn_ignore);
        btn_fixitnow= findViewById(R.id.btn_fixitnow);
        tv_nameRoom= findViewById(R.id.tv_nameRoom);
        tv_valueGasConcentration= findViewById(R.id.tv_valueGasConcentration);
        HomeActivity.badge.setVisibility(View.INVISIBLE);
        indexTopic = getIntent().getIntExtra("indexTopic",0);

        btn_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirm confirm = new Confirm();
                confirm.show(getSupportFragmentManager(),"activity_confirm");
            }
        });

        btn_fixitnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Driver: ", BackgroundService.mqttServiceSend.drvTopic.get(indexTopic));
                BackgroundService.mqttServiceSend.sendDataMQTT(BackgroundService.mqttServiceSend.DRV_PWM, BackgroundService.mqttServiceSend.drvTopic.get(indexTopic));
//                mqttService.sendDataMQTT("240", "minhanhlhpx5/feeds/fan");
                Intent intent = new Intent(WarningActivity.this, HomeActivity.class);
                intent.putExtra("Class", "WarningActivity");
                startActivity(intent);
                finish();
                //onBackPressed();
            }
        });
        loadValueInfo();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadValueInfo()
    {
        String value= getIntent().getStringExtra("value");
        String roomName= getIntent().getStringExtra("room_name");
        String room_id = Integer.toString(indexTopic);
        tv_nameRoom.setText(roomName);
        tv_valueGasConcentration.setText(value);
        FireBaseHelper.getInstance().sendHistoryData(room_id, Float.parseFloat(value), new FireBaseHelper.DataStatus() {
            @Override
            public <T> void dataIsLoaded(List<T> temp, List<String> keys) {

            }

            @Override
            public void dataIsSent() {

            }
        });
    }
}