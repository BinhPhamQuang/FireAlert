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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firealert.HomeActivity;
import com.example.firealert.MainActivity;
import com.example.firealert.R;
import com.example.firealert.RoomDetailActivity;
import com.example.firealert.WarningActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;




public class BackgroundService extends Service
{
    public int counter=0;
    public static boolean haveRead = false;
    public static String user_1;
    public static String pass_1;
    public static String user_2;
    public static String pass_2;

    public static MQTTService mqttServiceGet;
    public static MQTTService mqttServiceSend;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground();
            createNotificationChannel();
        }
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

        if(!haveRead) {
            haveRead = true;
            System.out.println("Start to set up adafruit");
            try {
                if (mqttServiceGet == null) {
                    mqttServiceGet = new MQTTService(this, user_1, pass_1, "123456", false);
                }
                if (mqttServiceSend == null) {
                    mqttServiceSend = new MQTTService(this, user_2, pass_2, "654321", true);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        mqttServiceGet.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(),"Can't connect to server get", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(mqttServiceGet.gasTopic.size()==0) return ;
                boolean subscribed = false;
                int indexTopic = 0;
                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
                    Log.d("Rooms",mqttServiceGet.gasTopic.get(i));
                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
                        indexTopic = i;
                        subscribed = true;
                    }
                }
                System.out.println(subscribed);
                if(!subscribed) {
                    return ;
                }
                Hashtable<String,String> mess = mqttServiceGet.getMessage(message.toString());
                Log.d("Size of rooms", String.valueOf(mqttServiceSend.rooms.size()));

                if(HomeActivity.list.get(0).get("1").equals("Not connected")){
                    HomeActivity.list.clear();
                    for (int i = 0; i < mqttServiceSend.rooms.size(); i++) {
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put("1", mqttServiceSend.rooms.get(i));
                        hashmap.put("2", "0");
                        HomeActivity.list.add(hashmap);
                    }
                }
                HomeActivity.list.get(indexTopic).put("2",mess.get("data"));
                HomeActivity.adapter.notifyDataSetChanged();

                if(RoomDetailActivity.list.size() != 0 && RoomDetailActivity.list != null){
                    RoomDetailActivity.list.get(indexTopic).put("2",mess.get("data"));
                    RoomDetailActivity.adapter.notifyDataSetChanged();
                }
//                Log.d("Info: ",mess.get("data"));
                Log.d("Message Arrived from: ", topic);
                Log.d("Info: ",message.toString());
                HomeActivity.GasConcentration = mess.get("data");
                if (Float.parseFloat(mess.get("data")) == 1)
                {
                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
                        Log.d("Buzzer",mqttServiceSend.buzzerTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
                        Log.d("Led",mqttServiceSend.ledTopic.get(indexTopic));
                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
                        Intent intent = new Intent(getApplicationContext(), WarningActivity.class);
                        String roomName = mqttServiceSend.rooms.get(indexTopic);
                        intent.putExtra("room_name", roomName);
                        intent.putExtra("value", mess.get("data"));
                        intent.putExtra("indexTopic",indexTopic);
                        intent.putExtra("user_1", user_1);
                        intent.putExtra("pass_1", pass_1);
                        intent.putExtra("user_2", user_2);
                        intent.putExtra("pass_2", pass_2);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.bell)
                                .setContentTitle("Warning !!!")
                                .setContentText("Your "+roomName+" high gas concentration !")
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(123,mBuilder.build());
                    }
                }
                else {
                    HomeActivity.badge.setVisibility(View.INVISIBLE);
                    Log.d("Buzzer",mqttServiceSend.buzzerTopic.get(indexTopic));
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER_OFF, mqttServiceSend.buzzerTopic.get(indexTopic));
                    Log.d("Led",mqttServiceSend.ledTopic.get(indexTopic));
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.LED_OFF, mqttServiceSend.ledTopic.get(indexTopic));
                    Log.d("Driver",mqttServiceSend.drvTopic.get(indexTopic));
                    mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM_OFF,mqttServiceSend.drvTopic.get(indexTopic));
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        mqttServiceSend.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(),"Can't connect to server send", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

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