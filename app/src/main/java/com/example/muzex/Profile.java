package com.example.muzex;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
      BottomNavigationView bottomNavigationView;
      Button savebtn,closebtn;
      TextView changet2,txtUsername;
      Button logout;
      CircleImageView circleImageView;
      private DatabaseReference databaseReference;
      private FirebaseAuth mAuth;
      private Uri imageUri;
      private String myUri="";
      private Dialog dialog;
      ProgressBar pbar;
      private StorageTask uploadTask;
      private StorageReference storageProfileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        txtUsername=findViewById(R.id.txtUsername);
        savebtn=findViewById(R.id.savebtn);
        dialog = new Dialog(Profile.this);

        circleImageView=findViewById(R.id.proimage);
        changet2=findViewById(R.id.changet2);
        logout=findViewById(R.id.logoutbtn);
        bottomNavigationView=findViewById(R.id.bottom_bar);
        pbar = findViewById(R.id.probar);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
        storageProfileReference= FirebaseStorage.getInstance().getReference().child("Profile Picture");

        dialog.setContentView(R.layout.alert_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_shape));
        dialog.setCancelable(false);


        Button btnYes=dialog.findViewById(R.id.btnYes);
        Button btnNo=dialog.findViewById(R.id.btnNo);
        getUserInfo();

        // Logout Dialog
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this,MainActivity.class));
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

            }
        });

        bottomNavigationView.setSelectedItemId(R.id.profile);


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });
        changet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CropImage.activity().setAspectRatio(1,1).start(Profile.this);
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent ,2);

            }
        });
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
                    case R.id.homes:
                        startActivity(new Intent(getApplicationContext(),List.class));
                        overridePendingTransition(0,0);
                        finish();
                        //return true;
                        break;
                    case R.id.profile:

                        // return true;
                        break;
                }

                return true;
            }
        });
    }

    private void getUserInfo()
    {
        txtUsername.setText("Hey, "+ mAuth.getCurrentUser().getDisplayName());
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("image")) {
                    String image = snapshot.child("image").getValue().toString();
                    Glide.with(circleImageView.getContext())
                            .load(image).placeholder(R.drawable.pro_pic).into(circleImageView);
                    pbar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            circleImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
        }*/
        if(requestCode==2 && resultCode==RESULT_OK && data !=null)
        {
            imageUri=data.getData();
            Glide.with(circleImageView.getContext())
                    .load(imageUri).placeholder(R.drawable.pro_pic).into(circleImageView);
        }
    }

    private void uploadProfileImage()
    {
       final ProgressDialog progressDialog=new ProgressDialog(this);
       progressDialog.setTitle("Set your profile");
       progressDialog.setMessage("Please wait, while we upload your data");
       progressDialog.setCancelable(false);
       progressDialog.show();
       if (imageUri != null)
       {
           UserProfileChangeRequest request=new  UserProfileChangeRequest.Builder()
                   .setPhotoUri(imageUri)
                   .build();
           mAuth.getCurrentUser().updateProfile(request);


           final StorageReference fileRef= storageProfileReference.child(mAuth.getCurrentUser().getUid() +".jpg");
           uploadTask=fileRef.putFile(imageUri);
           uploadTask.continueWithTask(new Continuation() {
               @Override
               public Object then(@NonNull Task task) throws Exception {
                   if (!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return fileRef.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if(task.isSuccessful())
                   {
                       Uri downloadUrl= task.getResult();
                       myUri=downloadUrl.toString();
                       HashMap<String, Object> userMap=new HashMap<>();
                       userMap.put("image",myUri);

                       databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                       progressDialog.dismiss();
                   }
               }
           });
       }
       else
       {
           progressDialog.dismiss();
           Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
       }

    }
}