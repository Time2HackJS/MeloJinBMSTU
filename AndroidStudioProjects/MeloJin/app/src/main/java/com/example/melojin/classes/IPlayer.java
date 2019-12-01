package com.example.melojin.classes;

import java.util.ArrayList;

public interface IPlayer {
    void playSong(final Song s, final int position, final ArrayList<Song> songList);
    void pauseSong();
    void stopSong();
    void stopPlayer();
}
