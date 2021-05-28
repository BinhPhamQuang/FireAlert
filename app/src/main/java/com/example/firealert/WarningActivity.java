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
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_bottom_sheet.Confirm;
import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.List;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    TextView tv_nameRoom;
    TextView tv_valueGasConcentration;
    MQTTService mqttService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);


        btn_ignore= findViewById(R.id.btn_ignore);
        btn_fixitnow= findViewById(R.id.btn_fixitnow);
        tv_nameRoom= findViewById(R.id.tv_nameRoom);
        tv_valueGasConcentration= findViewById(R.id.tv_valueGasConcentration);

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
                mqttService.sendDataMQTT("240", "biennguyenbk00/feeds/output.drv");
//                mqttService.sendDataMQTT("240", "minhanhlhpx5/feeds/fan");
                Intent intent = new Intent(WarningActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        loadValueInfo();



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

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadValueInfo()
    {
        String value= getIntent().getStringExtra("value");
        String roomName= getIntent().getStringExtra("room_name");
        int room_id= getIntent().getIntExtra("room_id",0);
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