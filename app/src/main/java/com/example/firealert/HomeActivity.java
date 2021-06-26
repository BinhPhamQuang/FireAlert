package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import com.example.firealert.DAO.FireBaseHelper;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.SystemClock;
import com.example.firealert.Service.BackgroundService;
import com.example.firealert.Service.Broadcast;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import com.example.firealert.Adapter.HomeAdapter;
import com.example.firealert.DTO.AdafruitAccount;
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
    public static final String CHANNEL_ID = "serviceBackground";
    ImageButton btnAddRoom;
    ImageButton btnNotification;
    View layoutAddroom;
    LinearLayout ll_close_layout;
    public static String user_1;
    public static String user_2;
    TextView txtWelcome;
    public static ArrayList<HashMap<String,String>> list = new ArrayList<>();
    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS ="2";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    public static HomeAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    private String userId, email, address, phone, username, houseId;
//    private AdafruitAccount adafruitAccount = new AdafruitAccount();
    private ArrayList<AdafruitAccount> listAccount = new ArrayList<>();
    public static CardView badge;
    public static String GasConcentration;
    public static String Room_name;


    @Override
    protected void onStart() {

        super.onStart();
    }
    private Context context;
    private BackgroundService backgroundService;
    private Intent mServiceIntent;
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return  false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent= new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this,Broadcast.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        readUserData(new FirebaseCallback() {
            @Override
            public void onCallback() {
                readAdafruitAccount(new FirebaseCallback() {
                    @Override
                    public void onCallback() {
                        backgroundService= new BackgroundService();
                        BackgroundService.user_1 = listAccount.get(0).getUsername();
                        BackgroundService.pass_1 = listAccount.get(0).getPassword();
                        BackgroundService.user_2 = listAccount.get(1).getUsername();
                        BackgroundService.pass_2 = listAccount.get(1).getPassword();
                        if(backgroundService !=null) {
                            mServiceIntent = new Intent(HomeActivity.this, backgroundService.getClass());
                            if (!isMyServiceRunning(backgroundService.getClass())) {
                                startService(mServiceIntent);
                            }
                        }
                    }
                });
            }
        });


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
                    intent.putExtra("room_name", Room_name);
                    intent.putExtra("value", GasConcentration);
//                    intent.putExtra("adafruitUsername", adafruitAccount.getUsername());
//                    intent.putExtra("adafruitPassword", adafruitAccount.getPassword());
                    intent.putExtra("user_1", listAccount.get(0).getUsername());
                    intent.putExtra("pass_1", listAccount.get(0).getPassword());
                    intent.putExtra("user_2", listAccount.get(1).getUsername());
                    intent.putExtra("pass_2", listAccount.get(1).getPassword());
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

                intent.putExtra("user_1", listAccount.get(0).getUsername());
                intent.putExtra("pass_1", listAccount.get(0).getPassword());
                intent.putExtra("user_2", listAccount.get(1).getUsername());
                intent.putExtra("pass_2", listAccount.get(1).getPassword());


                startActivity(intent);

            }
        });

        ImageButton btnTips = (ImageButton) findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),TipsActivity.class);


                intent.putExtra("user_1", listAccount.get(0).getUsername());
                intent.putExtra("pass_1", listAccount.get(0).getPassword());
                intent.putExtra("user_2", listAccount.get(1).getUsername());
                intent.putExtra("pass_2", listAccount.get(1).getPassword());

                startActivity(intent);
            }
        });

        ImageButton btnRoomDetail = (ImageButton) findViewById(R.id.btnRoomDetail);
        btnRoomDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(0).get("1").equals("Not connected")){
                    list.clear();
                    for (int i = 0; i < BackgroundService.mqttServiceSend.rooms.size(); i++) {
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put(ROOM_NAME, BackgroundService.mqttServiceSend.rooms.get(i));
                        hashmap.put(ROOM_GAS, "0");
                        list.add(hashmap);
                    }
                    adapter.notifyDataSetChanged();
                }

                Intent intent= new Intent(getApplicationContext(),RoomDetailActivity.class);
                intent.putExtra("listRoom",list);
                intent.putExtra("user_1", listAccount.get(0).getUsername());
                intent.putExtra("pass_1", listAccount.get(0).getPassword());
                intent.putExtra("user_2", listAccount.get(1).getUsername());
                intent.putExtra("pass_2", listAccount.get(1).getPassword());

                startActivity(intent);

            }
        });

        //__ THIS PART IS USE FOR RECYCLER VIEW (LIST OF ROOMS)
        AddItemsToRecyclerViewArrayList();
//        FireBaseHelper.getInstance().getHouseDevice(MainActivity.HousePath + "/rooms", new FireBaseHelper.DataStatus() {
//            @Override
//            public <T> void dataIsLoaded(List<T> temp, List<String> keys) {
//                List<Room> lst =  (List<Room>) temp;
//                int i = 0;
//                for(Room dataRoom: lst){
//                    if (i == 0) {
//                        list.get(0).put("1", dataRoom.name);
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        HashMap<String, String> hashmap = new HashMap<String, String>();
//                        hashmap.put(ROOM_NAME, dataRoom.name);
//                        hashmap.put(ROOM_GAS, "0");
//                        list.add(hashmap);
//                        adapter.notifyDataSetChanged();
//                    }
//                    i++;
//                }
//            }
//            @Override
//            public void dataIsSent() {
//
//            }
//        });
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        adapter = new HomeAdapter(list);
        HorizontalLayout = new LinearLayoutManager(
                HomeActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(adapter);
        //__ END OF THIS PART
//        System.out.println("Minh Anh end of part");

    }


    public void AddItemsToRecyclerViewArrayList()
    {
        // Adding items to ArrayList
        HomeActivity.list.clear();
        if(BackgroundService.mqttServiceGet == null){
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put(ROOM_NAME, "Not connected");
            hashmap.put(ROOM_GAS, "0");
            HomeActivity.list.add(hashmap);
        }
        else {
            list.clear();
            for (int i = 0; i < BackgroundService.mqttServiceSend.rooms.size(); i++) {
                HashMap<String, String> hashmap = new HashMap<String, String>();
                hashmap.put(ROOM_NAME, BackgroundService.mqttServiceSend.rooms.get(i));
                hashmap.put(ROOM_GAS, "0");
                list.add(hashmap);
            }
        }

    }


    private interface FirebaseCallback {
        void onCallback();
    }

//    private void setCallbackMQTT() {
//        if(!haveRead) {
//            haveRead = true;
//            System.out.println("Start to set up adafruit");
//            try {
//                if (mqttServiceGet == null) {
//                    mqttServiceGet = new MQTTService(this, listAccount.get(0).getUsername(), listAccount.get(0).getPassword(), "123456", false);
//                }
//                if (mqttServiceSend == null) {
//                    mqttServiceSend = new MQTTService(this, listAccount.get(1).getUsername(), listAccount.get(1).getPassword(), "654321", true);
//                }
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }
//        mqttServiceGet.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server get", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                if(mqttServiceGet.gasTopic.size()==0) return ;
//                boolean subscribed = false;
//                int indexTopic = 0;
//                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
//                    Log.d("Rooms",mqttServiceGet.gasTopic.get(i));
//                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
//                        indexTopic = i;
//                        subscribed = true;
//                    }
//                }
//                System.out.println(subscribed);
//                if(!subscribed) {
//                    return ;
//                }
//                Hashtable<String,String> mess = mqttServiceGet.getMessage(message.toString());
//                Log.d("Size of rooms", String.valueOf(HomeActivity.mqttServiceSend.rooms.size()));
//
//                if(list.get(0).get("1").equals("Not connected")){
//                    list.clear();
//                    for (int i = 0; i < HomeActivity.mqttServiceSend.rooms.size(); i++) {
//                        HashMap<String, String> hashmap = new HashMap<String, String>();
//                        hashmap.put(ROOM_NAME, HomeActivity.mqttServiceSend.rooms.get(i));
//                        hashmap.put(ROOM_GAS, "0");
//                        list.add(hashmap);
//                    }
//                }
//                list.get(indexTopic).put("2",mess.get("data"));
//                adapter.notifyDataSetChanged();
//
//                if(RoomDetailActivity.list.size() != 0 && RoomDetailActivity.list != null){
//                    RoomDetailActivity.list.get(indexTopic).put("2",mess.get("data"));
//                    RoomDetailActivity.adapter.notifyDataSetChanged();
//                }
////                Log.d("Info: ",mess.get("data"));
//                Log.d("Message Arrived from: ", topic);
//                Log.d("Info: ",message.toString());
//                GasConcentration = mess.get("data");
//                if (Float.parseFloat(mess.get("data")) == 1)
//                {
//                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
//                        Log.d("Buzzer",HomeActivity.mqttServiceSend.buzzerTopic.get(indexTopic));
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
//                        Log.d("Led",HomeActivity.mqttServiceSend.ledTopic.get(indexTopic));
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
//                        Intent intent = new Intent(HomeActivity.this, WarningActivity.class);
//                        // change this value for send data to another activity
//                        Room_name = mqttServiceSend.rooms.get(indexTopic);
//                        intent.putExtra("room_name", Room_name);
//                        //--------------------------------------------
//                        intent.putExtra("value", mess.get("data"));
//                        // must change this value by room_id
//                        intent.putExtra("indexTopic",indexTopic);
//                        intent.putExtra("user_1", listAccount.get(0).getUsername());
//                        intent.putExtra("pass_1", listAccount.get(0).getPassword());
//                        intent.putExtra("user_2", listAccount.get(1).getUsername());
//                        intent.putExtra("pass_2", listAccount.get(1).getPassword());
//                        startActivity(intent);
//
//                    }
//                }
//                else {
//                    HomeActivity.badge.setVisibility(View.INVISIBLE);
//                    Log.d("Buzzer",HomeActivity.mqttServiceSend.buzzerTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER_OFF, mqttServiceSend.buzzerTopic.get(indexTopic));
//                    Log.d("Led",HomeActivity.mqttServiceSend.ledTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.LED_OFF, mqttServiceSend.ledTopic.get(indexTopic));
//                    Log.d("Driver",HomeActivity.mqttServiceSend.drvTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM_OFF,mqttServiceSend.drvTopic.get(indexTopic));
//                }
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//
//        mqttServiceSend.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server send", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//            }
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//    }

    private void readAdafruitAccount(FirebaseCallback firebaseCallback) {
        DatabaseReference accountReference = FirebaseDatabase.getInstance().getReference("House").child(houseId).child("adafruit_account");
        accountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                AdafruitAccount account = snapshot.getValue(AdafruitAccount.class);
//                if (account != null) {
//                    adafruitAccount.setUsername(account.getUsername());
//                    adafruitAccount.setPassword(account.getPassword());
//                    firebaseCallback.onCallback();
//                }

                for(DataSnapshot data: snapshot.getChildren()){
                    AdafruitAccount account = data.getValue(AdafruitAccount.class);
                    listAccount.add(account);
                }
                user_1 = listAccount.get(0).getUsername();
                user_2 = listAccount.get(1).getUsername();
                firebaseCallback.onCallback();
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
                MainActivity.HousePath = houseId;
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