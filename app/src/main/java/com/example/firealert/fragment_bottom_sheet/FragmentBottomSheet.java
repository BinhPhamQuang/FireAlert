package com.example.firealert.fragment_bottom_sheet;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FragmentBottomSheet extends BottomSheetDialogFragment {
    EditText RoomName;
    DatabaseReference RoomDetails;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.add_room,container,false);
        Button btn_add = view.findViewById(R.id.btn_addRoom);
        RoomName = view.findViewById(R.id.txt_add);
        RoomDetails = FirebaseDatabase.getInstance().getReference().child("House/"+ MainActivity.HousePath);
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
        Query checkRoom = RoomDetails.orderByChild("name").equalTo(name);
        checkRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    RoomName.setError("This room have already have information!!");
                }
                else
                {
                    Room pNew = new Room("test buz","test drv","test gas", "test led", name);
                    RoomDetails.push().setValue(pNew);
                    Intent intent = new Intent(getContext(),HomeActivity.class);
                    startActivity(intent);
                    onDestroyView();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}
