package com.example.muzex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    DatabaseReference dataRef;
    ArrayList<SongReceive> songList=new ArrayList<>();
    SongListAdapter adapter;
    EditText etSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search);


        bottomNavigationView=findViewById(R.id.bottom_bar);
        etSearch = findViewById(R.id.editsearch);
        recyclerView=findViewById(R.id.recycleview);
        dataRef= FirebaseDatabase.getInstance().getReference();

        GridLayoutManager layoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        bottomNavigationView.setSelectedItemId(R.id.search);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.search:

                        break;
                    //return true;
                    case R.id.homes:
                        startActivity(new Intent(getApplicationContext(),List.class));
                        overridePendingTransition(0,0);
                        finish();
                        //return true;
                        break;
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

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable search) {
                if(search.toString().isEmpty()){
                    songList.clear();
                }else{
                    setdata(search.toString());
                }
            }
        });
    }
    private void setdata(String s) {
        dataRef.child("Song").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList.clear();
                for(DataSnapshot ds : snapshot.getChildren() ){
                    SongReceive sr = ds.getValue(SongReceive.class);
                    if(sr.getSongName().toLowerCase().contains(s.toLowerCase())) {
                        songList.add(sr);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new SongListAdapter(this,songList);
        recyclerView.setAdapter(adapter);

    }
}