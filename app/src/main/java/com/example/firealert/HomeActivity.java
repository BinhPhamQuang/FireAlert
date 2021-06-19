package com.example.firealert;

import androidx.annotation.NonNull;
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
import com.example.firealert.DTO.AdafruitAccount;
import com.example.firealert.DTO.UserData;
import com.example.firealert.Service.MQTTService;
import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    ImageButton btnNotification;
    View layoutAddroom;
    LinearLayout ll_close_layout;
    MQTTService mqttService;
    TextView txtWelcome;
    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS ="2";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    HomeAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    private String userId, email, address, phone, username, houseId;
    private AdafruitAccount adafruitAccount = new AdafruitAccount();

    public static CardView badge;
    public static String GasConcentration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        txtWelcome = (TextView) findViewById(R.id.tv_welcome);
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
        if(className != null && className.equals("Confirm")) {
            int extra = getIntent().getIntExtra("notification", 1);
            badge.setVisibility(extra);
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
                    intent.putExtra("adafruitUsername", adafruitAccount.getUsername());
                    intent.putExtra("adafruitPassword", adafruitAccount.getPassword());
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
                intent.putExtra("userId", userId);
                intent.putExtra("email", email);
                intent.putExtra("username", username);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("houseId", houseId);
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

        readUserData(new FirebaseCallback() {
            @Override
            public void onCallback() {
                readAdafruitAccount(new FirebaseCallback() {
                    @Override
                    public void onCallback() {
                        setCallbackMQTT();
                    }
                });
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


    private interface FirebaseCallback {
        void onCallback();
    }

    private void setCallbackMQTT() {
        try {
            mqttService = new MQTTService(this, adafruitAccount.getUsername(), adafruitAccount.getPassword());
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
                        intent.putExtra("adafruitUsername", adafruitAccount.getUsername());
                        intent.putExtra("adafruitPassword", adafruitAccount.getPassword());
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

    private void readAdafruitAccount(FirebaseCallback firebaseCallback) {
        DatabaseReference accountReference = FirebaseDatabase.getInstance().getReference("House").child(houseId).child("adafruit_account");
        accountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdafruitAccount account = snapshot.getValue(AdafruitAccount.class);
                if (account != null) {
                    adafruitAccount.setUsername(account.getUsername());
                    adafruitAccount.setPassword(account.getPassword());
                    firebaseCallback.onCallback();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUserData(FirebaseCallback firebaseCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                assert user != null;
                userId = user.getUser_id();
                email = user.getEmail();
                username = user.getUsername();
                address = user.getAddress();
                phone = user.getPhone();
                houseId = user.getHouse_id();
                String[] name = username.split(" ");
                txtWelcome.setText(String.format("Welcome %s !", name[name.length - 1]));
                firebaseCallback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}