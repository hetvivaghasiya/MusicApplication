package com.example.muzex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText user2, username, regpass, regpass2;
    Button regbtn;
    FirebaseAuth fauth;
    ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        user2 = findViewById(R.id.user2);
        username = findViewById(R.id.username);
        regpass = findViewById(R.id.regpass);
        regpass2 = findViewById(R.id.regpass2);
        regbtn = findViewById(R.id.regbtn);

        fauth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User");

        progressBar = findViewById(R.id.progress);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user2.getText().toString().trim();
                String password = regpass.getText().toString().trim();
                String strUsername = username.getText().toString().trim();
                String password2 = regpass2.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    user2.setError("please enter email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    regpass.setError("please enter password");
                    return;
                }
                if (password.length() < 6) {
                    regpass.setError("password must be more than 6 character");
                }
                if (TextUtils.isEmpty(strUsername)) {
                    username.setError("please enter Username");
                    return;
                }
                if (!password2.equals(password)) {
                    regpass2.setError("incorrect password");
                    Toast.makeText(Register.this, "Password Not matching", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    fauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                progressBar.setVisibility(View.GONE);
                                UserProfileChangeRequest request=new  UserProfileChangeRequest.Builder()
                                        .setDisplayName(strUsername)
                                        .build();
                                mAuth.getCurrentUser().updateProfile(request);
                                databaseReference.child(mAuth.getCurrentUser().getUid()).child("image").setValue("");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(Register.this, "account has been created", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

        });
    }
}