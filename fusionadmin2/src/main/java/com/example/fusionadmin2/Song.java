package com.example.fusionadmin2;

public class Song {
    private String SongName,songUrl,imageUri;

    public Song(String imageUri, String songName, String songUrl) {
        this.SongName = songName;
        this.songUrl = songUrl;
        this.imageUri = imageUri;
    }



    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
