package com.example.firealert.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firealert.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountTab2Adapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;

    public static final String ACCOUNT_NAME = "1";
    public static final String ACCOUNT_MAIL = "2";

    Activity activity;
    public AccountTab2Adapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    public ArrayList<HashMap<String, String>> getList() {
        return this.list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.object_account_tab2, parent, false);
        }
        else {
            row = convertView;
        }

        TextView tvEmail = row.findViewById(R.id.tv_mail);
        TextView tvName = row.findViewById(R.id.tv_name);
        tvEmail.setText(list.get(position).get(ACCOUNT_MAIL));
        tvName.setText(list.get(position).get(ACCOUNT_NAME));
        return row;
    }
}

