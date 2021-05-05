package com.example.firealert.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.firealert.R;


// The adapter class which
// extends RecyclerView Adapter
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyView> {

    // List with String type
    private ArrayList<HashMap<String, String>> list;

    public static final String ROOM_NAME = "1";
    public static final String ROOM_GAS = "2";

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView tv_roomName;
        TextView tv_roomGas;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);

            // initialise TextView with id
            tv_roomName = (TextView)view.findViewById(R.id.tv_roomName);
            tv_roomGas = (TextView)view.findViewById(R.id.tv_roomGas);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public HomeAdapter(ArrayList<HashMap<String, String>> list)
    {
        this.list = list;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.object_home, parent, false);

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final MyView holder, final int position)
    {

        // Set the text of each item of
        // Recycler view with the list items
        HashMap<String, String> map = list.get(position);
        holder.tv_roomName.setText(map.get(ROOM_NAME));
        holder.tv_roomGas.setText(map.get(ROOM_GAS));
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return list.size();
    }
}
