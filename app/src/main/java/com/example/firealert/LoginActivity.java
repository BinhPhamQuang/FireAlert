package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView btnSignUp;

    private ImageButton btnLogin;

    private TextInputLayout username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignUp= findViewById(R.id.btn_signUp);
        btnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.username_inputlayout);
        password = findViewById(R.id.password_inputlayout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                } else {
                    isUser();
                }
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();

        if (val.isEmpty()) {
            username.setError("Username cannot be empty");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private void isUser() {
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("password").getValue(String.class);

                    if (passwordFromDB != null && passwordFromDB.equals(userEnteredPassword)) {
                        username.setError(null);
                        username.setErrorEnabled(false);

                        Long userIdFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("user_id").getValue(Long.class);
                        String emailFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("email").getValue(String.class);
                        String addressFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("address").getValue(String.class);
                        String phoneFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("phone").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey()).child("username").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        intent.putExtra("user_id", userIdFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("address", addressFromDB);
                        intent.putExtra("phone", phoneFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        intent.putExtra("Class","LoginActivity");

                        startActivity(intent);

                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    username.setError("Wrong Username");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}