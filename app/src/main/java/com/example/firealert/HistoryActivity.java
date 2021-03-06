package com.example.firealert;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firealert.DTO.ListHistory;
import com.example.firealert.API.ApiService;
import com.example.firealert.Adapter.HistoryDataAdapter;
import com.example.firealert.DTO.History;
import com.example.firealert.DTO.User;
import com.example.firealert.Service.BackgroundService;
import com.example.firealert.Service.MQTTService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> list;
    private HistoryDataAdapter historyDataAdapter;
    private ListView lv_historydata;
    private String room_id;
    private String room_topic;
    private String room_name;


    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference reff;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        list = new ArrayList<HashMap<String, String>>();
        room_id= getIntent().getStringExtra("room_id");
        room_topic= getIntent().getStringExtra("room_topic");
        room_name = getIntent().getStringExtra("room_name");
        Log.d("Topic: " ,room_topic);
        reff= firebaseDatabase.getReference();


        ImageButton btnBack= findViewById(R.id.btnBack);
        lv_historydata=findViewById(R.id.lv_historydata);


        // must remove this line for release mode
        //------------------------
//        FireBaseHelper.getInstance().sendHistoryData(room_id, 0, new FireBaseHelper.DataStatus() {
//            @Override
//            public <T> void dataIsLoaded(List<T> temp, List<String> keys) {
//
//            }
//
//            @Override
//            public void dataIsSent() {
//
//            }
//        });

        list.clear();
        String feedname = room_topic.substring(room_topic.lastIndexOf("/") + 1);
        ApiService.apiService.getListHistoryData(feedname,getIntent().getStringExtra("user_1") , getIntent().getStringExtra("pass_1")).enqueue(new Callback<ListHistory>() {
            @Override
            public void onResponse(Call<ListHistory> call, Response<ListHistory> response) {
                Toast.makeText(getApplicationContext(), "Call Api Success", Toast.LENGTH_SHORT).show();
                ListHistory histories = response.body();
                List<History> result = histories.getResult();

                for (History history : result) {
                    HashMap<String,String> hashMap = new HashMap<String,String>();
                    String dates= history.getDate();
                    String value= history.getValue()+"";
                    hashMap.put(historyDataAdapter.DATE,dates);
                    hashMap.put(historyDataAdapter.VALUE,value);
                    list.add(hashMap);
                }

                lv_historydata.setAdapter((historyDataAdapter));
            }

            @Override
            public void onFailure(Call<ListHistory> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Call Api Error", Toast.LENGTH_SHORT).show();
            }
        });


        historyDataAdapter= new HistoryDataAdapter(this,list);
        //lv_historydata.setAdapter(historyDataAdapter);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btn_delete = (Button) findViewById(R.id.delete_btn);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete this room?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRoom(MainActivity.HousePath);

                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent= new Intent(getApplicationContext(),HistoryActivity.class);
                        //send room_id here
                        intent.putExtra("room_id",room_id);
                        //-------------------------------
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    private void deleteRoom(String house_id){
//        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference();
//        Query historyQuery = historyRef.child("History").orderByChild("house_id").equalTo(house_id);
//        historyQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot roomSnapshot: dataSnapshot.getChildren()) {
//                    History history= roomSnapshot.getValue(History.class);
//                    if (history.getId().equals(room_id))
//                    {
//                        roomSnapshot.getRef().removeValue();
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
        DatabaseReference houseRef = FirebaseDatabase.getInstance().getReference().child("House").child(house_id).child("rooms");
//        Query houseQuery = houseRef.child("House").orderByChild("house_id").equalTo(house_id);
        houseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dss: dataSnapshot.getChildren()) {
                    DataSnapshot roomChild = dss.child("name");
                    if (roomChild.getValue(String.class).equals(room_name))
                    {
                        dss.getRef().removeValue();
                        int t = Integer.parseInt(room_id);
                        System.out.println(t);
                        RoomDetailActivity.list.remove(t);
                        RoomDetailActivity.adapter.notifyDataSetChanged();

                        BackgroundService.mqttServiceGet.gasTopic.remove(t);

                        BackgroundService.mqttServiceSend.rooms.remove(t);
                        BackgroundService.mqttServiceSend.buzzerTopic.remove(t);
                        BackgroundService.mqttServiceSend.ledTopic.remove(t);
                        BackgroundService.mqttServiceSend.drvTopic.remove(t);

                        HomeActivity.list.remove(t);
                        HomeActivity.adapter.notifyItemRemoved(t);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
