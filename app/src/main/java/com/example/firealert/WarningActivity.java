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
import java.util.Hashtable;
import java.util.List;

public class WarningActivity extends AppCompatActivity {

    Button btn_ignore;
    Button btn_fixitnow;
    TextView tv_nameRoom;
    TextView tv_valueGasConcentration;
    MQTTService mqttServiceGet;
    MQTTService mqttServiceSend;
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
//                mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM, mqttServiceSend.drvTopic.get(indexTopic));
                Intent intent = new Intent(WarningActivity.this, HomeActivity.class);
                intent.putExtra("Class", "WarningActivity");
                startActivity(intent);
            }
        });
        loadValueInfo();



        try {

            mqttServiceGet = new MQTTService(this,MainActivity.Server_username_get,MainActivity.Server_password_get,"123456",false);
            mqttServiceSend = new MQTTService(this,MainActivity.Server_username_send,MainActivity.Server_password_send,"654321",true);
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

                System.out.println(mess);
                String data = mess.get("data");


                int indexTopic = 0;
                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
                        indexTopic = i;
                    }
                }
                if (data.equals("1")) {
                    Log.d("Message Arrived: ", topic);
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(WarningActivity.this, WarningActivity.class);
                        // change this value for send data to another activity
                        intent.putExtra("room_name", mqttServiceGet.rooms.get(indexTopic));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadValueInfo()
    {
        String value= getIntent().getStringExtra("value");
        String roomName= getIntent().getStringExtra("room_name");
        String room_id = Integer.toString(indexTopic);
        tv_nameRoom.setText(roomName);
        tv_valueGasConcentration.setText(value);

    }
}