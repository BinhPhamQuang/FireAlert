package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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