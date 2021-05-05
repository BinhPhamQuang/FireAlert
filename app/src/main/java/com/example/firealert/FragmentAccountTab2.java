package com.example.firealert;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firealert.Adapter.AccountTab2Adapter;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentAccountTab2 extends Fragment {

    private ArrayList<HashMap<String,String>> list;
    public static final String ACCOUNT_NAME = "1";
    public static final String ACCOUNT_MAIL ="2";

    public  static FragmentAccountTab2 getInstance()
    {
        FragmentAccountTab2 fragmentAccountTab2= new FragmentAccountTab2();
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
        populateList();
        ListView listView = (ListView) view.findViewById(R.id.listView);
        AccountTab2Adapter adapter = new AccountTab2Adapter(getActivity(), list);
        listView.setAdapter(adapter);
        return view;
    }

    private void populateList() {
        list = new ArrayList<HashMap<String, String>>();

        HashMap<String,String> hashmap2 = new HashMap<String,String>();
        hashmap2.put(ACCOUNT_NAME, "Bien Nguyen");
        hashmap2.put(ACCOUNT_MAIL, "nguyenvanbien2000@hcmut.edu.vn");
        list.add(hashmap2);

        HashMap<String,String> hashmap = new HashMap<String,String>();
        hashmap.put(ACCOUNT_NAME, "Nguyen Van Binh");
        hashmap.put(ACCOUNT_MAIL, "nvbinhk18@gmail.com");
        list.add(hashmap);

        HashMap<String,String> hashmap1 = new HashMap<String,String>();
        hashmap1.put(ACCOUNT_NAME, "Pham Quanh Binh");
        hashmap1.put(ACCOUNT_MAIL, "bnh.quang.18@gmail.com");
        list.add(hashmap1);

    }
}