package com.example.firealert.Adapter;

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

    private class ViewHolder {
        TextView tv_name;
        TextView tv_mail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountTab2Adapter.ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.object_account_tab2, null);
            holder = new AccountTab2Adapter.ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_mail = (TextView) convertView.findViewById(R.id.tv_mail);
            convertView.setTag(holder);
        } else {
            holder = (AccountTab2Adapter.ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map = list.get(position);
        holder.tv_name.setText(map.get(ACCOUNT_NAME));
        holder.tv_mail.setText(map.get(ACCOUNT_MAIL));

        return convertView;
    }
}

