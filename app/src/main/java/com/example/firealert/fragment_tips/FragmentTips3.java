package com.example.firealert.fragment_tips;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.firealert.R;


public class FragmentTips3 extends Fragment {

    public  static FragmentTips3 getInstance()
    {
        FragmentTips3 fragmentTips3= new FragmentTips3();
        return fragmentTips3;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_tips3,container,false);
        return view;
    }
}