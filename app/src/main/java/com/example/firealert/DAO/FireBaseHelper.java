package com.example.firealert.DAO;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.firealert.DTO.History;
import com.example.firealert.DTO.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FireBaseHelper {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rff;

    public interface DataStatus
    {
       <T> void dataIsLoaded(List<T> temp, List<String> keys);
            void dataIsSent();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendHistoryData(int room_id, float value, final DataStatus dataStatus)
    {
        rff= firebaseDatabase.getReference("History");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String key= rff.push().getKey();
        String currentDate=dtf.format(now).toString();
        History history= new History(currentDate, User.getInstance().getHouse_id(),room_id,value);
        rff.child(key).setValue(history).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.dataIsSent();
            }
        });
    }
}
