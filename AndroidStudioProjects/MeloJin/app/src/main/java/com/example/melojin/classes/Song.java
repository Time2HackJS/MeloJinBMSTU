package com.example.melojin.classes;
import android.widget.ImageView;

public class Song {
    private String name;
    private String artist;
    private String pic_folder;
    private String song_id;
    private Integer play_state;
    public ImageView imageView;

    public Song(String name, String artist, String pic_folder, Integer play_state, String song_id) {
        this.name = name;
        this.artist = artist;
        this.pic_folder = pic_folder;
        this.play_state = play_state;
        this.song_id = song_id;
    }

    public Song() {}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPic_folder() {
        return this.pic_folder;
    }

    public void setPic_folder(String pic_folder) {
        this.pic_folder = pic_folder;
    }

    public Integer getPlay_state() {
        return this.play_state;
    }

    public void setPlay_state(Integer play_state) {
        this.play_state = play_state;
    }

    public String getSong_id() {
        return this.song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
