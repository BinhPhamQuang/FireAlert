package com.example.firealert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {
    EditText txtEmailSignup, txtPasswordSignup, txtPasswordAgainSignup, txtYournameSignup, txtAddressSignup, txtPhoneNumberSignup;
    TextView btnSignUp;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtEmailSignup = findViewById(R.id.txt_emailSignup);
        txtPasswordSignup = findViewById(R.id.txt_passwordSignup);
        txtPasswordAgainSignup = findViewById(R.id.txt_passwordSignupAgain);
        txtYournameSignup = findViewById(R.id.txt_yournameSignup);
        txtAddressSignup = findViewById(R.id.txt_addressSignup);
        txtPhoneNumberSignup = findViewById(R.id.txt_phoneNumberSignup);
        btnSignUp = findViewById(R.id.btn_signUp);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailSignup, passwordSignup, passwordAgainSignup, yourNameSignup, addressSignup, phoneNumberSignup;
                emailSignup = txtEmailSignup.getText().toString();
                passwordSignup = txtPasswordSignup.getText().toString();
                passwordAgainSignup = txtPasswordAgainSignup.getText().toString();
                yourNameSignup = txtYournameSignup.getText().toString();
                addressSignup = txtAddressSignup.getText().toString();
                phoneNumberSignup = txtPhoneNumberSignup.getText().toString();

                if (isValidatedInformation(emailSignup, passwordSignup, passwordAgainSignup, yourNameSignup, addressSignup, phoneNumberSignup)) {
                    firebaseAuth.createUserWithEmailAndPassword(emailSignup, passwordSignup)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
                                        HashMap<String, String> userInformation = new HashMap<>();
                                        userInformation.put("user_id", user.getUid());
                                        userInformation.put("email", emailSignup);
                                        userInformation.put("password", passwordSignup);
                                        userInformation.put("username", yourNameSignup);
                                        userInformation.put("address", addressSignup);
                                        userInformation.put("phone", phoneNumberSignup);
                                        databaseReference.setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                }
                                                else {
                                                    Toast.makeText(SignUpActivity.this,"Sign up failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        finish();
                                    }
                                    else {
                                        try {
                                            throw task.getException();
                                        }
                                        catch(FirebaseAuthInvalidCredentialsException e) {
                                            txtEmailSignup.setError("The mail address is malformed!");
                                        }
                                        catch(FirebaseAuthUserCollisionException e) {
                                            txtEmailSignup.setError("There already exists the email address!");
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
                else {
                    return;
                }
            }
        });

    }

    private boolean isValidatedInformation(String email, String password, String passwordAgain, String yourName, String address, String phoneNumber) {
        if (email.isEmpty()){
            txtEmailSignup.setError("Email is required!");
            return false;
        }
        if (password.length() < 6) {
            if (password.isEmpty()) {
                txtPasswordSignup.setError("Password is required!");
                return false;
            }
            txtPasswordSignup.setError("Password length is more 6 letters!");
            return false;
        }
        if (!passwordAgain.equals(password)) {
            txtPasswordSignup.setError("Password & Confirm Password does not match");
            txtPasswordAgainSignup.setError("Password & Confirm Password does not match");
            return false;
        }
        if (yourName.isEmpty()) {
            txtYournameSignup.setError("Your name is required!");
            return false;
        }
        if (address.isEmpty()) {
            txtAddressSignup.setError("Address is required!");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            txtPhoneNumberSignup.setError("Phone number is required!");
            return false;
        }

        return true;
    }


}
