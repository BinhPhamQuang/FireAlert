package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firealert.Service.MQTTService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Hashtable;

public class PrivacyActivity extends AppCompatActivity {
    String username;
    TextView textView;
    EditText txtOldPassword, txtNewPassword, txtConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.txt_hello);
        txtOldPassword = findViewById(R.id.txt_oldPassword);
        txtNewPassword = findViewById(R.id.txt_newPassword);
        txtConfirmPassword = findViewById(R.id.txt_confirmPassword);

        username = getIntent().getStringExtra("username");
        textView.setText(String.format("Hello %s!", username));

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btn_saveChange = (Button) findViewById(R.id.btn_saveChange);
        btn_saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = txtOldPassword.getText().toString().trim();
                String newPassword = txtNewPassword.getText().toString().trim();
                if (isNewPasswordValidated()) {
                    updatePassword(oldPassword, newPassword);
                }
            }
        });

//        try {
//            mqttServiceGet = new MQTTService(this, getIntent().getStringExtra("user_1"),getIntent().getStringExtra("pass_1"),"123456",false);
//            mqttServiceSend = new MQTTService(this, getIntent().getStringExtra("user_2"), getIntent().getStringExtra("pass_2"),"654321",true);
//        }
//        catch (MqttException e) {
//            e.printStackTrace();
//        }
//
//        mqttServiceGet.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server get", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                Hashtable<String,String> mess = mqttServiceGet.getMessage(message.toString());
//
//                System.out.println(mess);
//                String data = mess.get("data");
//
//
//                int indexTopic = 0;
//                for (int i = 0; i < mqttServiceGet.gasTopic.size(); i++) {
//                    if (mqttServiceGet.gasTopic.get(i).equals(topic)) {
//                        indexTopic = i;
//                    }
//                }
//                if (data.equals("1")) {
//                    Log.d("Message Arrived: ", topic);
//                    if(HomeActivity.badge.getVisibility() == View.INVISIBLE) {
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER, mqttServiceSend.buzzerTopic.get(indexTopic));
//                        mqttServiceSend.sendDataMQTT(mqttServiceSend.LED, mqttServiceSend.ledTopic.get(indexTopic));
//                        Intent intent = new Intent(PrivacyActivity.this, WarningActivity.class);
//                        // change this value for send data to another activity
//                        intent.putExtra("room_name", mqttServiceGet.rooms.get(indexTopic));
//                        //--------------------------------------------
//                        intent.putExtra("value", mess.get("data"));
//                        // must change this value by room_id
//                        intent.putExtra("indexTopic",indexTopic);
//                        startActivity(intent);
//                    }
//                }
//                else {
//                    HomeActivity.badge.setVisibility(View.INVISIBLE);
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.SPEAKER_OFF, mqttServiceSend.buzzerTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.LED_OFF, mqttServiceSend.ledTopic.get(indexTopic));
//                    mqttServiceSend.sendDataMQTT(mqttServiceSend.DRV_PWM_OFF,mqttServiceSend.drvTopic.get(indexTopic));
//                }
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//        mqttServiceSend.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(),"Can't connect to server send", Toast.LENGTH_SHORT).show();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//            }
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
    }

    private void updatePassword(String oldPassword, String newPassword) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Handler handler = new Handler(Looper.getMainLooper());
        if (firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }
        // Before changing password, re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PrivacyActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtOldPassword.setError("Password is invalid!");
                        Toast.makeText(PrivacyActivity.this, "Password is invalid!", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private boolean isNewPasswordValidated() {
        String newPassword, confirmPassword;
        newPassword = txtNewPassword.getText().toString().trim();
        confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (newPassword.length() == 0) {
            txtNewPassword.setError("New password is empty!");
            Toast.makeText(PrivacyActivity.this, "New password is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 6) {
            txtNewPassword.setError("New password must be more 6 characters!");
            Toast.makeText(PrivacyActivity.this, "New password must be more 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            txtConfirmPassword.setError("Confirm password is not matching!");
            Toast.makeText(PrivacyActivity.this, "Confirm password is not matching!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}