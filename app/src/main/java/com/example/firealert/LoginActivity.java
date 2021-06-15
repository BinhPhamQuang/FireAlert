package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView btnSignUp;
    private ImageButton btnLogin;
    private TextInputLayout emailLayout, passwordLayout;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignUp= findViewById(R.id.btn_signUp);
        btnLogin = findViewById(R.id.btnLogin);
        emailLayout = findViewById(R.id.email_inputlayout);
        passwordLayout = findViewById(R.id.password_inputlayout);
        firebaseAuth = FirebaseAuth.getInstance();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                if (emailLayout.getEditText() == null || passwordLayout.getEditText() == null) {
                    return;
                }
                email = emailLayout.getEditText().getText().toString().trim();
                password = passwordLayout.getEditText().getText().toString().trim();
                if (!isValidatedInformation(email, password)) {
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            String path = "";
                            if(email.equals("cse@hcmut.edu.vn")){
                                path += "CSE";
                                MainActivity.SetUpServer("CSE_BBC1", "aio_VhCE38mvogdpc353vHMQl684Emfs",
                                        "CSE_BBC", "aio_qyBr29pmfJC09tUFB5n9Ap9AtIwD",path);
                            }
                            else {
                                MainActivity.SetUpServer("minhanhlhpx5", "aio_luee30ceekmTQiIGDRjAIf3RAxqw",
                                        "minhanhlhpx5", "aio_luee30ceekmTQiIGDRjAIf3RAxqw",path);
                            }

                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            passwordLayout.setError("Wrong Password");
                        }
                    });
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String path = "";
            if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("cse@hcmut.edu.vn")){
                System.out.println("Minh anh");
                path += "CSE";
                MainActivity.SetUpServer("CSE_BBC1", "aio_VhCE38mvogdpc353vHMQl684Emfs",
                        "CSE_BBC", "aio_qyBr29pmfJC09tUFB5n9Ap9AtIwD",path);
            }
            else {
                MainActivity.SetUpServer("minhanhlhpx5", "aio_luee30ceekmTQiIGDRjAIf3RAxqw",
                        "minhanhlhpx5", "aio_luee30ceekmTQiIGDRjAIf3RAxqw",path);
            }
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    private boolean isValidatedInformation(String username, String password) {
        if (username.isEmpty()) {
            emailLayout.setError("Username is empty!");
            return false;
        }
        else {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is empty!");
            return false;
        }
        else {
            passwordLayout.setError(null);
            passwordLayout.setErrorEnabled(false);
        }
        return true;
    }

}