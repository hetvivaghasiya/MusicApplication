package com.example.muzex;

import java.io.Serializable;

public class SongReceive implements Serializable {
    String imageUri,songName,songUrl;

    public SongReceive() {}

    public SongReceive(String imageUri, String songName, String songUrl) {
        this.imageUri = imageUri;
        this.songName = songName;
        this.songUrl = songUrl;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
