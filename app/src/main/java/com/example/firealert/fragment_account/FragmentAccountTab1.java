package com.example.firealert.fragment_account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.HomeActivity;
import com.example.firealert.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentAccountTab1 extends Fragment {

    private String userId;
    private String email;
    private String username;
    private String phone;
    private String address;
    private String houseId;
    EditText nameEditText;
    EditText addressEditText;
    EditText phoneEditText;
    Button btnSave;
    TextView textView;

    public FragmentAccountTab1(String userId, String email, String username, String phone, String address, String houseId) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.address = address;
        this.houseId = houseId;
    }

    public static FragmentAccountTab1 getInstance(String userId, String email, String username, String phone, String address, String houseId)
    {
        FragmentAccountTab1 fragmentAccountTab1;
        fragmentAccountTab1 = new FragmentAccountTab1(userId, email, username, phone, address, houseId);
        return fragmentAccountTab1;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_account_tab_1,container,false);
        textView = (TextView) view.findViewById(R.id.tv_hello);
        textView.setText(String.format("Hello %s!", username));
        nameEditText = (EditText) view.findViewById(R.id.txt_name);
        nameEditText.setText(username);
        addressEditText = (EditText) view.findViewById(R.id.txt_address);
        addressEditText.setText(address);
        phoneEditText = (EditText) view.findViewById(R.id.txt_phone);
        phoneEditText.setText(phone);


        btnSave = view.findViewById(R.id.btn_saveChange);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataChanged()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
                    databaseReference.child("username").setValue(nameEditText.getText().toString().trim());
                    databaseReference.child("address").setValue(addressEditText.getText().toString().trim());
                    databaseReference.child("phone").setValue(phoneEditText.getText().toString().trim());
                    Toast.makeText(getActivity(), "Your information has been updated!", Toast.LENGTH_SHORT).show();

                    // Remove all the previous activities from the back stack
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), "Your information is not changed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean isDataChanged() {
        if (!nameEditText.getText().toString().trim().equals(username)) {
            return true;
        }

        if (!addressEditText.getText().toString().trim().equals(address)) {
            return true;
        }

        if (!phoneEditText.getText().toString().trim().equals(phone)) {
            return true;
        }

        return false;
    }
}