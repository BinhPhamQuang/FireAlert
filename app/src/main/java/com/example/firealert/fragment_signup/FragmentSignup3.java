package com.example.firealert.fragment_signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.R;

public class FragmentSignup3 extends Fragment {

    public  static FragmentSignup3 getInstance()
    {
        FragmentSignup3 fragmentSignup3= new FragmentSignup3();
        return  fragmentSignup3;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_signup3,container,false);
        return  view;
    }
}
