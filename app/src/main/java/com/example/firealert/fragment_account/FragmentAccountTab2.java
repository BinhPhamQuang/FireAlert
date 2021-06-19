package com.example.firealert.fragment_account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.AccountActivity;
import com.example.firealert.Adapter.AccountTab2Adapter;
import com.example.firealert.DTO.UserData;
import com.example.firealert.HomeActivity;
import com.example.firealert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class FragmentAccountTab2 extends Fragment {

    private ArrayList<HashMap<String,String>> list;
    public static final String ACCOUNT_NAME = "1";
    public static final String ACCOUNT_MAIL ="2";
    private String houseId;
    EditText txtAddEmail;

    public FragmentAccountTab2(String houseId) {
        this.houseId = houseId;
        list = new ArrayList<HashMap<String, String>>();
    }

    public static FragmentAccountTab2 getInstance(String houseId)
    {
        FragmentAccountTab2 fragmentAccountTab2 = new FragmentAccountTab2(houseId);
        return  fragmentAccountTab2;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_tab_2,container,false);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        AccountTab2Adapter adapter = new AccountTab2Adapter(getActivity(), list);
        listView.setAdapter(adapter);

        txtAddEmail = view.findViewById(R.id.txt_addMail);
        Button btnAddEmail = view.findViewById(R.id.btn_addMail);
        btnAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtAddEmail.getText().toString().trim();
                if (isVerifiedEmail(email)) {
                    readHouse(houseId, new ReadHouseCallback() {
                        @Override
                        public void onCallback(DataSnapshot houseSnapshot) {
                            if (!houseSnapshot.exists()) {
                                Toast.makeText(getActivity(), "You cannot add another account!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            addEmail(email, new FirebaseCallback() {
                                @Override
                                public void onCallback() {
                                    adapter.notifyDataSetChanged();
                                    txtAddEmail.setText("");
                                }
                            });
                        }
                    });
                }
            }
        });


        readUsers(new FirebaseCallback() {
            @Override
            public void onCallback() {
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }


    private void readUsers(FirebaseCallback firebaseCallback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("House").child(houseId).child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    HashMap<String,String> userMap = new HashMap<String,String>();
                    userMap.put(ACCOUNT_NAME, user.getValue(UserData.class).getUsername());
                    userMap.put(ACCOUNT_MAIL, user.getValue(UserData.class).getEmail());
                    list.add(userMap);
                }

                firebaseCallback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface FirebaseCallback {
        void onCallback();
    }

    private interface ReadHouseCallback {
        void onCallback(DataSnapshot houseSnapshot);
    }

    private boolean isVerifiedEmail(String email) {
        if (email.isEmpty()) {
            txtAddEmail.setError("Invalid email address!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtAddEmail.setError("Invalid email address!");
            return false;
        }

        for (HashMap<String, String> userMap : list) {
            if (Objects.requireNonNull(userMap.get(ACCOUNT_MAIL)).equals(email)) {
                txtAddEmail.setError("Email already exists in list!");
                return false;
            }
        }

        return true;
    }

    private void readHouse(String houseId, ReadHouseCallback readHouseCallback) {
        DatabaseReference houseReference = FirebaseDatabase.getInstance().getReference("House").child(houseId);
        Log.e("HOUSE_ID", houseId);
        houseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readHouseCallback.onCallback(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addEmail(String email, FirebaseCallback firebaseCallback) {
        Query addedUserQuery = FirebaseDatabase.getInstance().getReference("User").orderByChild("email").equalTo(email);
        addedUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    Toast.makeText(getActivity(), "Account does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserData userData;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userData = dataSnapshot.getValue(UserData.class);
                    if (!userData.getHouse_id().equals("default") && !userData.getHouse_id().equals(houseId)) {
                        Toast.makeText(getActivity(), "The account has been added to another house!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference addedUserReference = FirebaseDatabase.getInstance().getReference("User").child(userData.getUser_id());
                    addedUserReference.child("house_id").setValue(houseId);
                    userData.setHouse_id(houseId);
                    DatabaseReference userInHouseReference = FirebaseDatabase.getInstance().getReference("House").child(houseId).child("users").child(userData.getUser_id());
                    HashMap<String, String> userInformation = new HashMap<>();
                    userInformation.put("user_id", userData.getUser_id());
                    userInformation.put("email", userData.getEmail());
                    userInformation.put("username", userData.getUsername());
                    userInformation.put("address", userData.getAddress());
                    userInformation.put("phone", userData.getPhone());
                    userInformation.put("house_id", houseId);
                    userInHouseReference.setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            HashMap<String,String> userMap = new HashMap<String,String>();
                            userMap.put(ACCOUNT_NAME, userData.getUsername());
                            userMap.put(ACCOUNT_MAIL, userData.getEmail());
                            list.add(userMap);
                            firebaseCallback.onCallback();
                        }
                    });
                    break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Adding email failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}