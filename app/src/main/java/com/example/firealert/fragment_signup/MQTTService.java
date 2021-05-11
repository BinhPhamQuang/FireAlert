package com.example.firealert.fragment_signup;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTService {
    final String serverUri= "tcp://io.adafruit.com:1883";
    final String clientID="123456";
    final String subscriptionTopic ="quangbinh/feeds/test";
    final String username="quangbinh";
    final String password="aio_grNV97RinHfoAC6LXFOOACyoU7PI";
    public MqttAndroidClient mqttAndroidClient;
    public MQTTService(Context context) throws MqttException {
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
                    subscribeToTopic();
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
    private  void subscribeToTopic()
    {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
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
}
