package com.example.firealert.fragment_signup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.R;

public class FragmentSignup2 extends Fragment {
    public  static FragmentSignup2 getInstance()
    {
        FragmentSignup2 fragmentSignup2= new FragmentSignup2();
        return  fragmentSignup2;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_signup2,container,false);
        return view;
    }
}
