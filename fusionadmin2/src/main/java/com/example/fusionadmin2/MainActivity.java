package com.example.fusionadmin2;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    ImageButton btnup;
    private boolean checkPermission=false;
    Uri uriSong,imageUri;
    Button btnUpload;
    ImageView imageView;
    TextView tvUpload;
    String SongName,songUrl,strImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView( R.layout.activity_main);
        tvUpload=findViewById(R.id.txtUpload);
        btnup=findViewById(R.id.btnup);
        btnUpload=findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent ,2);
            }
        });

        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickSong();

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadSongToFirebaseStorage();
            }
        });
    }

    private void pickSong()
    {
        Intent intent_upload=new Intent();
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        intent_upload.setType("audio/*,image/*");
        startActivityForResult(intent_upload,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                uriSong=data.getData();

                Cursor mcursor=getApplicationContext().getContentResolver().query(uriSong,null,null,null,null);
                int indexedName=mcursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                mcursor.moveToFirst();
                SongName=mcursor.getString(indexedName);
                mcursor.close();
                tvUpload.setText(SongName);

            }
        }
        if(requestCode==2 && resultCode==RESULT_OK && data !=null)
        {
            imageUri=data.getData();
            Glide.with(imageView.getContext())
                    .load(imageUri).placeholder(R.drawable.custom_image).into(imageView);
        }
    }

    private void uploadSongToFirebaseStorage()
    {
        StorageReference storageReference1= FirebaseStorage.getInstance().getReference().child("Songs").child(uriSong.getLastPathSegment());
        StorageReference storageReference2= FirebaseStorage.getInstance().getReference().child("Songs Image").child(uriSong.getLastPathSegment());
        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child("Song").push();

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        storageReference1.putFile(uriSong).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong=uriTask.getResult();

                storageReference2.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri urlImg=uriTask.getResult();
                        strImg = urlImg.toString();
                        songUrl=urlSong.toString();
                        Song obj=new Song(strImg,SongName,songUrl);
                        dataref.setValue(obj);//Upload Data to database

                    }
                });

                Toast.makeText(MainActivity.this, "Song uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                tvUpload.setText("Select Song");
                imageView.setImageResource(R.drawable.custom_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                int currentProgress=(int)progress;
                progressDialog.setMessage("Uploaded "+ currentProgress+"%");
            }
        });
    }

    private boolean validatePermission()
    {
        Dexter.withContext(MainActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        checkPermission=true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        checkPermission=false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return checkPermission;
    }
}