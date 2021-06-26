package com.example.firealert.fragment_bottom_sheet;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firealert.DAO.FireBaseHelper;
import com.example.firealert.DTO.Room;
import com.example.firealert.HomeActivity;
import com.example.firealert.MainActivity;
import com.example.firealert.R;
import com.example.firealert.RoomDetailActivity;
import com.example.firealert.Service.BackgroundService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FragmentBottomSheet extends BottomSheetDialogFragment {
    EditText RoomName,RoomDrv,RoomGas,RoomLed,RoomBuzzer;
    DatabaseReference RoomDetails;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.add_room,container,false);
        Button btn_add = view.findViewById(R.id.btn_addRoom);
        RoomName = view.findViewById(R.id.txt_name);
        RoomDrv = view.findViewById(R.id.txt_driver);
        RoomGas = view.findViewById(R.id.txt_gas);
        RoomLed = view.findViewById(R.id.txt_led);
        RoomBuzzer = view.findViewById(R.id.txt_buzzer);
        RoomDetails = FirebaseDatabase.getInstance().getReference().child("House/"+ MainActivity.HousePath + "/rooms");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertHouseDetail();

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void insertHouseDetail(){
        String name = RoomName.getText().toString();
        String drv = HomeActivity.user_2 + "/feeds/" + RoomDrv.getText().toString();
        String gas = HomeActivity.user_1 + "/feeds/" + RoomGas.getText().toString();
        String led = HomeActivity.user_2 + "/feeds/" + RoomLed.getText().toString();
        String buzzer = HomeActivity.user_2 + "/feeds/" + RoomBuzzer.getText().toString();

        Query checkRoom = RoomDetails.orderByChild("name").equalTo(name);
        checkRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    RoomName.setError("This room have already have information!!");
                }
                else
                {
                    Room pNew = new Room(buzzer,drv,gas,led,name);
                    RoomDetails.push().setValue(pNew);
                    BackgroundService.mqttServiceSend.buzzerTopic.add(buzzer);
                    BackgroundService.mqttServiceSend.drvTopic.add(drv);
                    BackgroundService.mqttServiceSend.ledTopic.add(led);
                    BackgroundService.mqttServiceSend.rooms.add(name);

                    BackgroundService.mqttServiceGet.gasTopic.add(gas);
                    BackgroundService.mqttServiceGet.subscribeToTopic(gas);
                    int newSize = BackgroundService.mqttServiceGet.gasTopic.size();

                    Log.d("Size", String.valueOf(newSize));
                    for(int i=0;i<newSize;i++)
                        Log.d("Rooms",BackgroundService.mqttServiceGet.gasTopic.get(i));

                    System.out.println(HomeActivity.list.get(0).get("1").equals("Not connected"));
                    if(HomeActivity.list.get(0).get("1").equals("Not connected")){
                        HomeActivity.list.clear();
                        for (int i = 0; i < newSize; i++) {
                            HashMap<String, String> hashmap = new HashMap<String, String>();
                            hashmap.put("1", BackgroundService.mqttServiceSend.rooms.get(i));
                            hashmap.put("2", "0");
                            HomeActivity.list.add(hashmap);
                            if(i!=0) HomeActivity.adapter.notifyItemInserted(i);
                        }
                    }
                    else {
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put("1", name);
                        hashmap.put("2", "0");
                        HomeActivity.list.add(hashmap);
                        HomeActivity.adapter.notifyItemInserted(newSize-1);

                    }
                    HomeActivity.adapter.notifyItemRangeChanged(0,newSize);
                    HomeActivity.adapter.notifyDataSetChanged();


                    if(RoomDetailActivity.list.size() !=0 && RoomDetailActivity.list !=null) {
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put("1", name);
                        hashmap.put("2", "0");
                        RoomDetailActivity.list.add(hashmap);
                        RoomDetailActivity.adapter.notifyDataSetChanged();
                    }

                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}
