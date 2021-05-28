package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firealert.Adapter.HistoryDataAdapter;
import com.example.firealert.DAO.FireBaseHelper;
import com.example.firealert.DTO.History;
import com.example.firealert.DTO.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> list;
    private HistoryDataAdapter historyDataAdapter;
    private ListView lv_historydata;
    private int room_id;
    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference reff;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        list = new ArrayList<HashMap<String, String>>();
        room_id= getIntent().getIntExtra("room_id",-1);
        reff= firebaseDatabase.getReference();


        ImageButton btnBack= findViewById(R.id.btnBack);
        lv_historydata=findViewById(R.id.lv_historydata);


        // must remove this line for release mode
        room_id=11;
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
        FireBaseHelper.getInstance().getHistory(User.getInstance().getHouse_id(), room_id, new FireBaseHelper.DataStatus() {
            @Override
            public <T> void dataIsLoaded(List<T> temp, List<String> keys) {
                List<History> histories =  (List<History>) temp;
                list.clear();


                for (History history:histories)
                {
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
            public void dataIsSent() {

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
    }



//    private void getDataHistory()
//    {
//
//        int home_id= User.getInstance().getHouse_id();
//        reff= reff.child("History");
//        reff.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                list.clear();
//                for (DataSnapshot dss:snapshot.getChildren())
//                {
//                    // must remove this line in release mode
//                    room_id=11;
//                    //------------------
//                    if (dss.child("house_id").getValue().toString().equals(home_id+"") && dss.child("room_id").getValue().toString().equals(room_id+"")  )
//                    {
//                        HashMap<String,String> hashMap = new HashMap<String,String>();
//                        String dates= dss.child("date").getValue().toString();
//                        String value= dss.child("value").getValue().toString();
//                        hashMap.put(historyDataAdapter.DATE,dates);
//                        hashMap.put(historyDataAdapter.VALUE,value);
//                        list.add(hashMap);
//                    }
//                }
//                lv_historydata.setAdapter(historyDataAdapter);
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
    private void createSample()
    {
        HashMap<String,String> hashMap1 = new HashMap<String,String>();
        hashMap1.put(historyDataAdapter.DATE, "24/05/2021 14:00");
        hashMap1.put(historyDataAdapter.VALUE, "12.00");

        HashMap<String,String> hashMap2 = new HashMap<String,String>();
        hashMap2.put(historyDataAdapter.DATE, "24/05/2021 14:00");
        hashMap2.put(historyDataAdapter.VALUE, "12.00");
        HashMap<String,String> hashMap3 = new HashMap<String,String>();
        hashMap3.put(historyDataAdapter.DATE, "24/05/2021 14:00");
        hashMap3.put(historyDataAdapter.VALUE, "12.00");
        list.add(hashMap1);
        list.add(hashMap2);
        list.add(hashMap3);
    }
}