package com.example.melojin;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<Song> songList = new ArrayList<>();
        final ListView songListView = findViewById(R.id.listView);

        Log.d(TAG, "onCreate: Started.");

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

        final SongListAdapter adapter = new SongListAdapter(this, R.layout.adapter_view_layout, songList);
        songListView.setAdapter(adapter);

        // Set an item click listener for ListView
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

        // Завтрашний Я. Разберись, пожалуйста, с этим говном. Это поиск.

        /*
        final EditText searchField = findViewById(R.id.fieldSearch);


        searchField.addTextChangedListener(new TextWatcher() {

            String fieldValue = searchField.getText().toString();

            ArrayList<Song> songSearchList = new ArrayList<>();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                for (int i = 0; i < songList.size(); i++) {
                    Song currsong = (Song) songList.get(i);
                    if (currsong.getArtist().contains(fieldValue) || currsong.getName().contains(fieldValue)) {
                        songSearchList.add(currsong);
                    }
                }
            }
        });
        */
    }
}
