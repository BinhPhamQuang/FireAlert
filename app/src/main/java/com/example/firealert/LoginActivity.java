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
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private TextView btnSignUp, txtForgottenPassword;
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
        txtForgottenPassword = findViewById(R.id.txt_forgottenPassword);
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

                            if (isEmailVerified()) {
                                String path = "";
                                if(email.equals("cse@hcmut.edu.vn")){
                                    path += "CSE";
                                    MainActivity.SetUpServer("CSE_BBC1", "aio_VhCE38mvogdpc353vHMQl684Emfs",
                                            "CSE_BBC", "aio_qyBr29pmfJC09tUFB5n9Ap9AtIwD",path);
                                    MainActivity.SetUpServer("minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",
                                            "biennguyenbk00", "aio_iboi96HqYYZyzroSlH4yp6byPKCj",path);
                                }
                                else {
                                    MainActivity.SetUpServer("minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",
                                            "minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",path);
                                }

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                return;
                            }
                            startActivity(new Intent(getApplicationContext(), VerifyEmailActivity.class));
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

        txtForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgottenPasswordActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (isEmailVerified()) {
                String path = "";
                if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("cse@hcmut.edu.vn")){
                    path += "CSE";
                    MainActivity.SetUpServer("CSE_BBC1", "aio_VhCE38mvogdpc353vHMQl684Emfs",
                            "CSE_BBC", "aio_qyBr29pmfJC09tUFB5n9Ap9AtIwD",path);
                    MainActivity.SetUpServer("minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",
                            "biennguyenbk00", "aio_iboi96HqYYZyzroSlH4yp6byPKCj",path);
                }
                else {
                    MainActivity.SetUpServer("minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",
                            "minhanhlhpx5", "aio_vmMk58XXUXgMXEPKq5JnjggZR0Xl",path);
                }

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
            else {
                startActivity(new Intent(getApplicationContext(), VerifyEmailActivity.class));
            }
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

    private boolean isEmailVerified() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser.isEmailVerified();
    }

}