package com.example.muzex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
   Button btn;
   EditText User,logpass;
   FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.logbtn);
        User=findViewById(R.id.user);
        logpass=findViewById(R.id.logpass);
        auth=FirebaseAuth.getInstance();

        TextView text=(TextView)findViewById(R.id.sign);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), List.class));
           finish();
        }


      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String e = User.getText().toString().trim();
            String log = logpass.getText().toString().trim();
            if (!e.isEmpty() && !log.isEmpty()){
                auth.signInWithEmailAndPassword(e,log).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), List.class));
                            finish();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "User not fount please signup account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                if(e.isEmpty()){
                    User.setError("Enter Email");
                }
                if(log.isEmpty()){
                    logpass.setError("Enter Password");
                }
            }


        }
    });

    }
}