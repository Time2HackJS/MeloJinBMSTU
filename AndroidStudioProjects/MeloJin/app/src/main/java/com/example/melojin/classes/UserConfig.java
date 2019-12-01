package com.example.melojin.classes;

import android.media.MediaPlayer;
import android.view.View;

import java.util.ArrayList;

public class UserConfig {
    private static UserConfig instance;
    private UserConfig() {}

    public static synchronized UserConfig getInstance() {
        if (instance == null) {
            instance = new UserConfig();
        }

        return instance;
    }

    public User currentUser = new User();
    public ArrayList<Song> songList = new ArrayList<>();
    public ArrayList<Song> savedSongs = new ArrayList<>();
    public ArrayList<User> users = new ArrayList<>();
    public Song clickedSong;
    public Integer prevPosition;
    public View preView;
    public MediaPlayer player;
    public Song currentSong;
    public Song prevSong;
    public String searchString;
}
