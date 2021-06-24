package com.example.firealert.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firealert.R;
import com.example.firealert.WarningActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Timer;
import java.util.TimerTask;

//public class BackgroundService extends Service {
//    private  boolean isRunning;
//    private Context context;
//    private Thread backgroundThread;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        this.context=this;
//        this.isRunning=false;
//        this.backgroundThread= new Thread(myTask);
//    }
//
//    private Runnable myTask = new Runnable() {
//        @Override
//        public void run() {
//            System.out.println("Back ground service running !");
//            stopSelf();
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        this.isRunning=false;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(!this.isRunning)
//        {
//            this.isRunning=true;
//            this.backgroundThread.start();
//        }
//        return START_STICKY;
//    }
//}


public class BackgroundService extends Service
{
    public int counter=0;
    private MQTTService mqttService;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    public static final String CHANNEL_ID = "serviceBackground";
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TEST";
            String description ="TEST";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        MqttInit();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Broadcast.class);
        this.sendBroadcast(broadcastIntent);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void MqttInit()
    {
        try {
            mqttService = new MQTTService(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message arrived");
                System.out.println(message.toString());
                int indexTopic = 0;
                for (int i = 0; i < mqttService.gasTopic.length; i++) {
                    if (mqttService.gasTopic[i].equals(topic)) {
                        indexTopic = i;
                    }
                }
                if (Float.parseFloat(message.toString()) >= 10)
                {
                    Intent intent1   = new Intent(getApplicationContext(), WarningActivity.class);
                    intent1.putExtra("room_name", mqttService.rooms[indexTopic]);
                    //--------------------------------------------
                    intent1.putExtra("value", message.toString());
                    // must change this value by room_id
                    intent1.putExtra("indexTopic",indexTopic);
                    PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                    String roomName=mqttService.rooms[indexTopic];
                    //--------------------------------------------
                    String value= message.toString();
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.next)
                            .setContentTitle("Warning !!!")
                            .setContentText("Your "+roomName+" high gas concentration !")
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(123,mBuilder.build());

                }



            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}