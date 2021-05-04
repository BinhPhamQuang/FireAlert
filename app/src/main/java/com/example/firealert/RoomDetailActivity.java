package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;


import com.example.firealert.Adapter.RoomDetailAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class RoomDetailActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String GAS ="2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        ImageButton btn_back= (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        populateList();

        ListView listView = (ListView) findViewById(R.id.listView);
        RoomDetailAdapter adapter = new RoomDetailAdapter(this, list);
        listView.setAdapter(adapter);

    }

    private void populateList() {
        list = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> hashmap2 = new HashMap<String,String>();
        hashmap2.put(ROOM_NAME, "Room 1");
        hashmap2.put(GAS, "0.00");
        list.add(hashmap2);

        HashMap<String,String> hashmap = new HashMap<String,String>();
        hashmap.put(ROOM_NAME, "Room 2");
        hashmap.put(GAS, "1.00");
        list.add(hashmap);

        HashMap<String,String> hashmap1 = new HashMap<String,String>();
        hashmap1.put(ROOM_NAME, "Room 3");
        hashmap1.put(GAS, "9.20");
        list.add(hashmap1);

    }
}
