package com.example.melojin.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    public String userName;
    public String userEmail;
    public ArrayList<Song> songList = new ArrayList<>();
    public Integer prevPosition;
    public View preView;
}
