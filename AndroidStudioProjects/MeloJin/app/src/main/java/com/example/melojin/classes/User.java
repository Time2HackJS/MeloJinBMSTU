package com.example.melojin.classes;

import java.util.ArrayList;

public class User {
    public String name, email, current_song;
    public ArrayList<String> friends = new ArrayList<>();
    public ArrayList<String> songs = new ArrayList<>();
    public ArrayList<User> friendList = new ArrayList<>();

    public User() {}

    public User(String name, String email, ArrayList<String> friends, ArrayList<String> savedSongs, String current_song) {
        this.name = name;
        this.email = email;
        this.friends = friends;
        this.songs = savedSongs;
        this.current_song = current_song;
    }
}
