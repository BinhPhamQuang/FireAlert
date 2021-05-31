package com.example.firealert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Adapter.HomeAdapter;
import com.example.firealert.DAO.FireBaseHelper;
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    ImageButton btnNotification;
    View layoutAddroom;
    LinearLayout ll_close_layout;
    MQTTService mqttService;
    TextView textView;
    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS ="2";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    HomeAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    public static CardView badge;
    public static String GasConcentration;
    String userId;
    String email, address, phone, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        textView = (TextView) findViewById(R.id.tv_welcome);

        btnNotification = (ImageButton) findViewById(R.id.btnNotification);
        ll_close_layout =findViewById(R.id.ll_close_layout);
        ll_close_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddroom.setVisibility(View.INVISIBLE);
            }
        });
        badge = findViewById(R.id.badge);
        badge.setVisibility(View.INVISIBLE);

        String className = getIntent().getStringExtra("Class");
        if(className.equals("Confirm")) {
            int extra = getIntent().getIntExtra("notification", 1);
            badge.setVisibility(extra);
        } else if (className.equals("LoginActivity")) {
            userId = getIntent().getStringExtra("user_id");
            email = getIntent().getStringExtra("email");
            address = getIntent().getStringExtra("address");
            phone = getIntent().getStringExtra("phone");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            textView.setText("Welcome " + username + " !");
        } else {

        }


        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        String[] splitDate = formattedDate.split(",");

        TextView tv_currentDate = (TextView) findViewById(R.id.tv_currentDate);
        tv_currentDate.setText(splitDate[1]  + ", " + splitDate[2]);

        btnNotification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(badge.getVisibility()== View.INVISIBLE){
                    Toast.makeText(getApplicationContext(),"No Notices Right Now!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(HomeActivity.this, WarningActivity.class);
                    intent.putExtra("room_name", "Room 1");
                    intent.putExtra("value", GasConcentration);
                    startActivity(intent);
                }
            }
        });


        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentBottomSheet bottomsheet = new FragmentBottomSheet();
                bottomsheet.show(getSupportFragmentManager(),"add_room");
            }
        });

        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SettingsActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        ImageButton btnTips = (ImageButton) findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),TipsActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnRoomDetail = (ImageButton) findViewById(R.id.btnRoomDetail);
        btnRoomDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),RoomDetailActivity.class);
                startActivity(intent);
            }
        });

        //__ THIS PART IS USE FOR RECYCLER VIEW (LIST OF ROOMS)
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);



        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        adapter = new HomeAdapter(list);
        HorizontalLayout = new LinearLayoutManager(
                HomeActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(adapter);
        //__ END OF THIS PART

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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                int indexTopic = 0;
                for (int i = 0; i < mqttService.gasTopic.length; i++) {
                    if (mqttService.gasTopic[i].equals(topic)) {
                        indexTopic = i;
                    }
                }
                if (Float.parseFloat(message.toString()) >= 10)
                {
                    Log.d("Message Arrived: ", topic);
                    GasConcentration = message.toString();
                    if(badge.getVisibility() == View.INVISIBLE) {
                        mqttService.sendDataMQTT(mqttService.SPEAKER, mqttService.buzzerTopic[indexTopic]);
                        mqttService.sendDataMQTT(mqttService.LED, mqttService.ledTopic[indexTopic]);


                        Intent intent = new Intent(HomeActivity.this, WarningActivity.class);
                        // change this value for send data to another activity
                        intent.putExtra("room_name", mqttService.rooms[indexTopic]);
                        //--------------------------------------------
                        intent.putExtra("value", message.toString());
                        // must change this value by room_id
                        intent.putExtra("indexTopic",indexTopic);
                        startActivity(intent);
                    }
                }
                else if (Float.parseFloat(message.toString()) >= 2){
                    badge.setVisibility(View.INVISIBLE);
                    mqttService.sendDataMQTT(mqttService.SPEAKER_OFF, mqttService.buzzerTopic[indexTopic]);
                    mqttService.sendDataMQTT(mqttService.LED_OFF, mqttService.ledTopic[indexTopic]);
                }
                else {
                    badge.setVisibility(View.INVISIBLE);
                    mqttService.sendDataMQTT(mqttService.SPEAKER_OFF, mqttService.buzzerTopic[indexTopic]);
                    mqttService.sendDataMQTT(mqttService.LED_OFF, mqttService.ledTopic[indexTopic]);
                    mqttService.sendDataMQTT(mqttService.DRV_PWM_OFF, mqttService.drvTopic[indexTopic]);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void AddItemsToRecyclerViewArrayList()
    {
        // Adding items to ArrayList
        list = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> hashmap = new HashMap<String,String>();
        HashMap<String,String> hashmap1 = new HashMap<String,String>();
        HashMap<String,String> hashmap2 = new HashMap<String,String>();
        HashMap<String,String> hashmap3 = new HashMap<String,String>();
        HashMap<String,String> hashmap4 = new HashMap<String,String>();

        hashmap.put(ROOM_NAME, "Room 1");
        hashmap.put(ROOM_GAS, "0.00");
        list.add(hashmap);

        hashmap1.put(ROOM_NAME, "Room 2");
        hashmap1.put(ROOM_GAS, "1.00");
        list.add(hashmap1);

        hashmap2.put(ROOM_NAME, "Room 3");
        hashmap2.put(ROOM_GAS, "9.20");
        list.add(hashmap2);

        hashmap3.put(ROOM_NAME, "Room 4");
        hashmap3.put(ROOM_GAS, "5.20");
        list.add(hashmap3);

        hashmap4.put(ROOM_NAME, "Room 5");
        hashmap4.put(ROOM_GAS, "1.20");
        list.add(hashmap4);
    }
}