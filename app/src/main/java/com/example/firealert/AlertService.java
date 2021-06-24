package com.example.firealert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firealert.Service.MQTTService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class AlertService extends Service {

    MQTTService mqttService;
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
    public AlertService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
                int indexTopic = 0;
                for (int i = 0; i < mqttService.gasTopic.length; i++) {
                    if (mqttService.gasTopic[i].equals(topic)) {
                        indexTopic = i;
                    }
                }
                if (Float.parseFloat(message.toString()) >= 10)
                {
                    Intent intent1   = new Intent(getApplicationContext(),WarningActivity.class);
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

//                   Intent intent1   = new Intent(getApplicationContext(),SettingsActivity.class);
//                   intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                   startActivity(intent1);
                }



            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}


