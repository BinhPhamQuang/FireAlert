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
//    final String subscriptionTopic ="minhanhlhpx5/feeds/gas-concentration";
//    final String username="minhanhlhpx5";
//    final String password="aio_luee30ceekmTQiIGDRjAIf3RAxqw";
//    final String username="biennguyenbk00";
//    final String password="aio_iboi96HqYYZyzroSlH4yp6byPKCj";
    String username;
    String password;

//    public final String[] gasTopic ={"biennguyenbk00/feeds/gas","biennguyenbk00/feeds/gas2"};
//    public final String[] drvTopic ={"biennguyenbk00/feeds/output.drv","biennguyenbk00/feeds/output.drv2"};
//    public final String[] ledTopic ={"biennguyenbk00/feeds/output.led","biennguyenbk00/feeds/output.led2"};
//    public final String[] buzzerTopic ={"biennguyenbk00/feeds/output.buzzer","biennguyenbk00/feeds/output.buzzer2"};
    public final String[] gasTopic ={"buitandanh1612/feeds/gas"};
    public final String[] drvTopic ={"buitandanh1612/feeds/drv"};
    public final String[] ledTopic ={"buitandanh1612/feeds/led"};
    public final String[] buzzerTopic ={"buitandanh1612/feeds/buzzer"};

    public final String[] rooms ={"Living room","Kitchen"};


    public final String LED         = "[\"id\":\"1\",\"name\":\"LED\",\"data\":\"1\",\"unit\":\"\"]";
    public final String LED_OFF     = "[\"id\":\"1\",\"name\":\"LED\",\"data\":\"2\",\"unit\":\"\"]";
    public final String SPEAKER     = "[\"id\":\"3\",\"name\":\"SPEAKER\",\"data\":\"1000\",\"unit\":\"\"]";
    public final String SPEAKER_OFF = "[\"id\":\"3\",\"name\":\"SPEAKER\",\"data\":\"0\",\"unit\":\"\"]";
    public final String DRV_PWM     = "[\"id\":\"10\",\"name\":\"DRV_PWM\",\"data\":\"240\",\"unit\":\"\"]";
    public final String DRV_PWM_OFF = "[\"id\":\"10\",\"name\":\"DRV_PWM\",\"data\":\"0\",\"unit\":\"\"]";

    public MqttAndroidClient mqttAndroidClient;
    public MQTTService(Context context, String username, String password) throws MqttException {
        this.username = username;
        this.password = password;
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
                    for (String topic: gasTopic) { subscribeToTopic(topic); }
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

}
