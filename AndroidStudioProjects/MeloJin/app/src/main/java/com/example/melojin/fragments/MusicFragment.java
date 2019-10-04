package com.example.melojin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.melojin.R;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.SongListAdapter;

import java.util.ArrayList;

public class MusicFragment extends Fragment {

    private ListView songListView;
    private ArrayList<Song> songList = new ArrayList<>();
    private SongListAdapter adapter;

    public static MusicFragment newInstance() {
        MusicFragment musicFragment = new MusicFragment();

        return musicFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        // reference
        songListView = rootView.findViewById(R.id.listView);

        // Create the Song objects
        Song s1 = new Song("Scatman's World", "John Scatman", "poster_1", 0);
        Song s2 = new Song("ME & CREED", "SawanoHiroyuki[nZk]:Sayuri", "poster_2", 0);
        Song s3 = new Song("The Court of the Crimson King", "King Crimson", "poster_3", 0);
        Song s4 = new Song("Eonian", "ELISA", "poster_4", 0);
        Song s5 = new Song("Resister", "ASCA", "poster_5", 0);
        Song s6 = new Song("2045", "MAN WITH A MISSION", "poster_6", 0);
        Song s7 = new Song("Narrative", "SawanoHiroyuki[nZk]:LiSA", "poster_7", 0);
        Song s8 = new Song("In The Way", "Stephen Stills", "poster_8", 0);
        Song s9 = new Song("I Just Died In Your Arms Tonight", "Cutting Crew", "poster_9", 0);

        // Add the Song objects to an ArrayList
        songList.add(s1);
        songList.add(s2);
        songList.add(s3);
        songList.add(s4);
        songList.add(s5);
        songList.add(s6);
        songList.add(s7);
        songList.add(s8);
        songList.add(s9);

        // adapter
        adapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, songList);
        songListView.setAdapter(adapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Integer prevPosition = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                Song selectedSong = (Song) parent.getItemAtPosition(position);

                if (selectedSong.getPlay_state() == 0 || selectedSong.getPlay_state() == 2) {
                    selectedSong.setPlay_state(1);
                } else selectedSong.setPlay_state(2);

                if (prevPosition != position) {
                    Song prevSong = (Song) parent.getItemAtPosition(prevPosition);
                    prevSong.setPlay_state(0);
                }

                prevPosition = position;
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
}
