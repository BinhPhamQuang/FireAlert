package com.example.firealert;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.firealert.R;


public class FragmentTips4 extends Fragment {

    public  static FragmentTips4 getInstance()
    {
        FragmentTips4 fragmentTips4= new FragmentTips4();
        return fragmentTips4;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_tips4,container,false);
        return view;
    }
}