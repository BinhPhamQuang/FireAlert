package com.example.firealert;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.firealert.Adapter.HistoryDataAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,String>> list;
    private HistoryDataAdapter historyDataAdapter;
    private ListView lv_historydata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        list = new ArrayList<HashMap<String, String>>();
        createSample();


        ImageButton btnBack= findViewById(R.id.btnBack);

        lv_historydata=findViewById(R.id.lv_historydata);
        historyDataAdapter= new HistoryDataAdapter(this,list);
        Log.e("hihihi",historyDataAdapter.getCount()+"");
        lv_historydata.setAdapter(historyDataAdapter);

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //onBackPressed();
//                //finish();
//            }
//        });
    }

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