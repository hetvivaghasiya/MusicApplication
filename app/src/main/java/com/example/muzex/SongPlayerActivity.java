package com.example.muzex;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SongPlayerActivity extends AppCompatActivity {

    ToggleButton tbPlayPause;
    Button btnBack, btnNext, btnPre;
    ImageView ivImage, backImage;
    TextView tvSongName, tvCurrentTime, tvTotalTime;
    SeekBar seekBar;
    BlurView blurView;
    ArrayList<SongReceive> songList;
    SongReceive currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    Animation btn_up, btn_down;




    @Override
    public void onBackPressed() {
        MyMediaPlayer.getInstance().stop();
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_song_player);

        tbPlayPause = findViewById(R.id.togglepause);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnPre = findViewById(R.id.btnPre);
        ivImage = findViewById(R.id.ivSongImage);
        tvSongName = findViewById(R.id.tvSongName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        blurView = findViewById(R.id.blurView);
        backImage = findViewById(R.id.backImage);




        btn_up = AnimationUtils.loadAnimation(this, R.anim.button_up);
        btn_down = AnimationUtils.loadAnimation(this, R.anim.button_down);

        seekBar = findViewById(R.id.playerseekbar);

        //Blur Background
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(20);

        tvSongName.setSelected(true);
        songList = (ArrayList<SongReceive>) getIntent().getSerializableExtra("List");
        SetResourcesWithSong();

        SongPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    tvCurrentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition()));
                    if (mediaPlayer.isPlaying()) {
                        tbPlayPause.setBackgroundResource(R.drawable.pause);
                    } else {
                        tbPlayPause.setBackgroundResource(R.drawable.btn_play);
                    }
                }
                new Handler().postDelayed(this, 50);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                PLayNextSong();
            }
        });

    }




   

    void SetResourcesWithSong(){
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        tvSongName.setText(currentSong.getSongName());

        Glide.with(ivImage)
                .load(currentSong.getImageUri())
                .placeholder(R.drawable.custom_image)
                .into(ivImage);
        tbPlayPause.setBackgroundResource(R.drawable.pause);
        tbPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayPause();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLayNextSong();
            }
        });
//        download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DownloadFile(SongPlayerActivity.this,currentSong.getSongName(),DIRECTORY_DOWNLOADS,currentSong.getSongUrl() );
//            }
//        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLayPreviousSong();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                finish();
            }
        });
        Glide.with(backImage)
                .load(currentSong.getImageUri())
                .placeholder(R.drawable.custom_image)
                .into(backImage);
        PLaySong();

    }

//    private void DownloadFile(Context context,String fileName,String directory,String url) {
//        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri= Uri.parse(url);
//        DownloadManager.Request request=new DownloadManager.Request(uri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(directory,fileName);
//        downloadManager.enqueue(request);
//
//    }

    void PLaySong(){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(currentSong.getSongUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            tvTotalTime.setText(convertToMMSS(mediaPlayer.getDuration()));
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void PLayNextSong(){
        if(MyMediaPlayer.currentIndex == songList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        SetResourcesWithSong();
    }

    void PLayPreviousSong(){
        if(MyMediaPlayer.currentIndex == 0){
            return;
        }
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        SetResourcesWithSong();
    }

    void PlayPause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }

    private String convertToMMSS(long milliseconds)
    {
        String timerString="";
        String secondString,minStr;
        int hours=(int)(milliseconds / (1000 * 60 * 60));
        int minutes=(int)(milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds=(int)((milliseconds % (1000 * 60 * 60 )) % (1000 * 60) / 1000);
        if(hours > 0)
        {
            timerString = hours + ":";
        }
        if(minutes < 10){
            minStr = "0"+minutes;
        }else{
            minStr = minutes+"";
        }
        if (seconds < 10)
        {
            secondString = "0"+seconds;
        }else {
            secondString = ""+seconds;
        }
        timerString = timerString + minStr + ":" + secondString;
        return  timerString;
    }
}

