package com.example.melojin.classes;

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
}
