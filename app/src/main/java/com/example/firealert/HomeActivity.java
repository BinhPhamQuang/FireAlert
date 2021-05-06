package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firealert.Adapter.HomeAdapter;
import com.example.firealert.fragment_bottom_sheet.FragmentBottomSheet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnAddRoom;
    View layoutAddroom;
    LinearLayout ll_close_layout;

    private ArrayList<HashMap<String,String>> list;
    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS ="2";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    HomeAdapter adapter;
    LinearLayoutManager HorizontalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layoutAddroom = findViewById(R.id.lb_addroom);
        btnAddRoom = (ImageButton) findViewById(R.id.btnAddRoom);
        ll_close_layout =findViewById(R.id.ll_close_layout);
        ll_close_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddroom.setVisibility(View.INVISIBLE);
            }
        });


        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        String[] splitDate = formattedDate.split(",");

        TextView tv_currentDate = (TextView) findViewById(R.id.tv_currentDate);
        tv_currentDate.setText(splitDate[1]  + ", " + splitDate[2]);


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


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        adapter = new HomeAdapter(list);
        HorizontalLayout = new LinearLayoutManager(
                HomeActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(adapter);
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