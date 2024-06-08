package com.example.muzex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class List extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    DatabaseReference dataRef;
    ArrayList<SongReceive> songList=new ArrayList<SongReceive>();
    SongListAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list);

        bottomNavigationView=findViewById(R.id.bottom_bar);
        recyclerView=findViewById(R.id.recycleview);
        bottomNavigationView.setSelectedItemId(R.id.homes);
        progressBar = findViewById(R.id.proBar);

        dataRef= FirebaseDatabase.getInstance().getReference();

        GridLayoutManager layoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        setdata();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),Search.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    //return true;
                    case R.id.profile:
                        Intent intent = new Intent(getApplicationContext(),Profile.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                        // return true;
                        break;
                }

                return true;
            }
        });

    }

    private void setdata() {
        dataRef.child("Song").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for(DataSnapshot ds : snapshot.getChildren() ){
                    SongReceive sr = ds.getValue(SongReceive.class);
                    songList.add(sr);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new SongListAdapter(this,songList);
        recyclerView.setAdapter(adapter);
    }
}