package com.example.firealert.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firealert.HistoryActivity;
import com.example.firealert.R;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;

public class HistoryDataAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> list;
    public static final String DATE = "1";
    public static final String VALUE = "2";
    private Activity activity;
    public  HistoryDataAdapter(Activity activity, ArrayList<HashMap<String, String>> list)
    {
        this.activity=activity;
        this.list=list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryDataAdapter.ViewHolder viewHolder;
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        if (convertView==null)
        {
            convertView= layoutInflater.inflate(R.layout.layout_item_history,null);
            viewHolder= new HistoryDataAdapter.ViewHolder();
            viewHolder.tv_dates= convertView.findViewById(R.id.tv_dates);
            viewHolder.tv_values=convertView.findViewById(R.id.tv_values);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> map = list.get(position);
        viewHolder.tv_dates.setText(map.get(DATE));
        viewHolder.tv_values.setText(map.get(VALUE));
        return convertView;
    }


    private class ViewHolder
    {
        TextView tv_dates;
        TextView tv_values;
    }
}
