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

public class RoomDetailAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;

    public static final String ROOM_NAME = "1";
    public static final String GAS = "2";

    Activity activity;
    public RoomDetailAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
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
        TextView tv_roomName;
        TextView tv_gas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomDetailAdapter.ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.object_room_detail, null);
            holder = new RoomDetailAdapter.ViewHolder();
            holder.tv_roomName = (TextView) convertView.findViewById(R.id.tv_roomName);
            holder.tv_gas = (TextView) convertView.findViewById(R.id.tv_gas);
            convertView.setTag(holder);
        } else {
            holder = (RoomDetailAdapter.ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map = list.get(position);
        holder.tv_roomName.setText(map.get(ROOM_NAME));
        holder.tv_gas.setText(map.get(GAS));

        return convertView;
    }
}

