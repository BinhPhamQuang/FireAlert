package com.example.firealert.DAO;

import androidx.annotation.NonNull;

import com.example.firealert.DTO.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseHelper {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rff;

    public interface DataStatus
    {
       <T> void dataIsLoaded(List<T> temp, List<String> keys);
    }
    private FireBaseHelper()
    {
        firebaseDatabase= FirebaseDatabase.getInstance();
    }
    public static FireBaseHelper getInstance()
    {
        FireBaseHelper fireBaseHelper= new FireBaseHelper();
        return  fireBaseHelper;
    }
    public void getHistory(int house_id,int room_id,final DataStatus dataStatus)
    {
        List<History> histories= new ArrayList<>();
        rff=  firebaseDatabase.getReference("History");
        rff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                histories.clear();
                List<String> keys= new ArrayList<>();
                for (DataSnapshot dss:snapshot.getChildren())
                {
                    keys.add(dss.getKey());
                    History history= dss.getValue(History.class);
                    if (history.getHouse_id()==house_id && history.getRoom_id()==room_id)
                    {
                        histories.add(history);
                    }

                }
                dataStatus.dataIsLoaded(histories,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
