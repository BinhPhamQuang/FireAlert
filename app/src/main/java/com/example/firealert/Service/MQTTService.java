package com.example.firealert.Service;

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

import java.nio.charset.Charset;

public class MQTTService {
    final String serverUri= "tcp://io.adafruit.com:1883";
    final String clientID="123456";
    final String subscriptionTopic ="minhanhlhpx5/feeds/gas-concentration";
    final String username="minhanhlhpx5";
    final String password="aio_luee30ceekmTQiIGDRjAIf3RAxqw";
//    final String subscriptionTopic ="biennguyenbk00/feeds/gas";
//    final String username="biennguyenbk00";
//    final String password="aio_iboi96HqYYZyzroSlH4yp6byPKCj";
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

}
