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
import com.example.firealert.DTO.Room;
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
import com.google.firebase.database.snapshot.Index;

import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    ImageButton btnNotification;
    View layoutAddroom;
    LinearLayout ll_close_layout;

    TextView txtWelcome;
    private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS ="2";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    HomeAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String address, phone, username;
    public static CardView badge;
    public static String GasConcentration;
    MQTTService mqttServiceGet;
    MQTTService mqttServiceSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String server = getIntent().getStringExtra("Username");
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        txtWelcome = (TextView) findViewById(R.id.tv_welcome);
        btnNotification = (ImageButton) findViewById(R.id.btnNotification);
        ll_close_layout =findViewById(R.id.ll_close_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                assert user != null;
                username = user.getUsername();
                address = user.getAddress();
                phone = user.getPhone();
                String[] name = username.split(" ");
                txtWelcome.setText(String.format("Welcome %s !", name[name.length - 1]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                    startActivity(intent);
                }
            }
        });


        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String[] name = username.split(" ");
//                if(name[0].equals("CSE")){
//                    Toast.makeText(getApplicationContext(),"This's just a test account", Toast.LENGTH_SHORT).show();
//                }
//                else
                    {
                    FragmentBottomSheet bottomsheet = new FragmentBottomSheet();
                    bottomsheet.show(getSupportFragmentManager(), "add_room");
                }
            }
        });

        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SettingsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
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
                AddItemsToRecyclerViewArrayList();
                Intent intent= new Intent(getApplicationContext(),RoomDetailActivity.class);
                intent.putExtra("listRoom",list);
                startActivity(intent);
            }
        });

        //__ THIS PART IS USE FOR RECYCLER VIEW (LIST OF ROOMS)
//        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
//
//
//
//        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
//
//        adapter = new HomeAdapter(list);
//        HorizontalLayout = new LinearLayoutManager(
//                HomeActivity.this, LinearLayoutManager.HORIZONTAL,false);
//        recyclerView.setLayoutManager(HorizontalLayout);
//        recyclerView.setAdapter(adapter);
        //__ END OF THIS PART


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
                System.out.println(message.toString());

                String data = mess.get("data");
                System.out.println(data);
                int indexTopic = 0;
                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
                        indexTopic = i;
                    }
                }

                assert data != null;
                if (data.equals("1")) {
                    System.out.println(true);
                    Log.d("Message Arrived: ", topic);
                    GasConcentration = mess.get("data");
                    if(badge.getVisibility() == View.INVISIBLE) {
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(HomeActivity.this, WarningActivity.class);
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
                    System.out.println(false);
                    badge.setVisibility(View.INVISIBLE);
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

    public void AddItemsToRecyclerViewArrayList()
    {
            list.clear();
            for (int i = 0; i < mqttServiceGet.rooms.size(); i++) {

                HashMap<String, String> hashmap = new HashMap<String, String>();
                hashmap.put(ROOM_NAME, mqttServiceGet.rooms.get(i));
                hashmap.put(ROOM_GAS, "0");
                list.add(hashmap);
            }
    }
}