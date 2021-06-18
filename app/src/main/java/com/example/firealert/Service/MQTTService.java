package com.example.firealert.Service;

import android.content.Context;
import android.util.Log;

import com.example.firealert.DAO.FireBaseHelper;
import com.example.firealert.DTO.Room;
import com.example.firealert.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MQTTService {
    final String serverUri= "tcp://io.adafruit.com:1883";
    private final String clientID = "123456";
    private final String username;
    private final String password;
    public final boolean send;

    public  ArrayList<String> gasTopic = new ArrayList<String>();
    public  ArrayList<String> drvTopic = new ArrayList<String>();
    public  ArrayList<String> ledTopic = new ArrayList<String>();
    public  ArrayList<String> buzzerTopic = new ArrayList<String>();
    public  ArrayList<String> rooms = new ArrayList<String>();

    public final String LED         = "{\"id\":\"1\",\"name\":\"LED\",\"data\":\"1\",\"unit\":\"\"}";
    public final String LED_OFF     = "{\"id\":\"1\",\"name\":\"LED\",\"data\":\"2\",\"unit\":\"\"}";
    public final String SPEAKER     = "{\"id\":\"3\",\"name\":\"SPEAKER\",\"data\":\"1000\",\"unit\":\"\"}";
    public final String SPEAKER_OFF = "{\"id\":\"3\",\"name\":\"SPEAKER\",\"data\":\"0\",\"unit\":\"\"}";
    public final String DRV_PWM     = "{\"id\":\"10\",\"name\":\"DRV_PWM\",\"data\":\"240\",\"unit\":\"\"}";
    public final String DRV_PWM_OFF = "{\"id\":\"10\",\"name\":\"DRV_PWM\",\"data\":\"0\",\"unit\":\"\"}";

    public MqttAndroidClient mqttAndroidClient;
    public MQTTService(Context context, String username, String password, String clientID, boolean send) throws MqttException {
        this.username = username;
        this.password = password;
        this.send = send;

        System.out.println(this.username);
        System.out.println(this.password);

        mqttAndroidClient = new MqttAndroidClient(context,serverUri,clientID);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("mqtt",serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.w("mqtt","error !");

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("Mqtt",message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        connect();
        SetUpHouseDevice();

    }

    public void setCallback(MqttCallbackExtended callback)
    {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect() throws MqttException {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions= new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt","Failed to connection to: "+serverUri+exception.toString());

                }
            });
        }
        catch (MqttException ex)
        {
            ex.printStackTrace();
        }
    }
    private  void subscribeToTopic(String topic)
    {
        try {

            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed !");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt","Subscribed fail !");
                }
            });

        }
        catch (MqttException ex)
        {
            ex.printStackTrace();
        }
    }

    public void sendDataMQTT(String data, String topic) {
        MqttMessage msg = new MqttMessage();
        msg.setId(123456);
        msg.setQos(0);
        msg.setRetained(true);
        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        Log.d("MQTT","Publish:"+ msg);
        try {
            this.mqttAndroidClient.publish(topic, msg);
        } catch ( MqttException e) {
            Log.d("MQTT","sendDataMQTT: cannot send message");
        }
    }
    private void SetUpHouseDevice(){
        FireBaseHelper.getInstance().getHouseDevice(MainActivity.HousePath, new FireBaseHelper.DataStatus() {
            @Override
            public <T> void dataIsLoaded(List<T> temp, List<String> keys) {
                List<Room> lst =  (List<Room>) temp;
                for(Room dataRoom: lst){
                    if(!send) {
                        gasTopic.add(dataRoom.gas);
                        System.out.println(dataRoom.gas);
                    }
                    else {
                        drvTopic.add(dataRoom.drv);
                        System.out.println(dataRoom.drv);
                        ledTopic.add(dataRoom.led);
                        System.out.println(dataRoom.led);
                        buzzerTopic.add(dataRoom.buzzer);
                        System.out.println(dataRoom.buzzer);
                    }
                    rooms.add(dataRoom.name);

                }
                for (String topic: gasTopic){
                    subscribeToTopic(topic);
                }
            }
            @Override
            public void dataIsSent() {

            }
        });


    }

    public Hashtable<String,String> getMessage(String message){
        Hashtable<String,String> result = new Hashtable<>();
        int index = message.indexOf("\"id\"",0);
        int begin = message.indexOf("\"",index+4);
        int end = message.indexOf("\"",begin+1);

        result.put("id",message.substring(begin+1,end));

        index = message.indexOf("\"name\"",end+1);
        begin = message.indexOf("\"",index+6);
        end = message.indexOf("\"",begin+1);
        result.put("name",message.substring(begin+1,end));

        index = message.indexOf("\"data\"",end+1);
        begin = message.indexOf("\"",index+6);
        end = message.indexOf("\"",begin+1);
        result.put("data",message.substring(begin+1,end));

        index = message.indexOf("\"unit\"",end+1);
        begin = message.indexOf("\"",index+6);
        end = message.indexOf("\"",begin+1);
        result.put("unit",message.substring(begin+1,end));


        return result;
    }
}
