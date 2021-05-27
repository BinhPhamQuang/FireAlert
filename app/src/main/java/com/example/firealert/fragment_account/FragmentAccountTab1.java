package com.example.firealert.fragment_account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.R;


public class FragmentAccountTab1 extends Fragment {

    public  static FragmentAccountTab1 getInstance()
    {
        FragmentAccountTab1 fragmentAccountTab1= new FragmentAccountTab1();
        return  fragmentAccountTab1;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_account_tab_1,container,false);
        return view;
    }
}