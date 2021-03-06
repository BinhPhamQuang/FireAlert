package com.example.firealert.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


public class Broadcast extends BroadcastReceiver
{

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Intent background= new Intent(context, BackgroundService.class);
//        context.startForegroundService(background);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BackgroundService.class));
        } else {
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}