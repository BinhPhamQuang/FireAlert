package com.example.firealert.fragment_account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.R;


public class FragmentAccountTab1 extends Fragment {

    private String username;
    private String phone;
    private String address;
    EditText nameEditText;
    EditText addressEditText;
    EditText phoneEditText;
    TextView textView;

    public FragmentAccountTab1(String username, String phone, String address) {
        this.username=username;
        this.phone=phone;
        this.address=address;
    }

    public static FragmentAccountTab1 getInstance(String username, String phone, String address)
    {
        FragmentAccountTab1 fragmentAccountTab1= new FragmentAccountTab1(username, phone, address);
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
        textView.setText("Hello " + username + "!");
        nameEditText = (EditText) view.findViewById(R.id.txt_name);
        nameEditText.setText(username);
        addressEditText = (EditText) view.findViewById(R.id.txt_address);
        addressEditText.setText(address);
        phoneEditText = (EditText) view.findViewById(R.id.txt_phone);
        phoneEditText.setText(phone);
        return view;
    }
}