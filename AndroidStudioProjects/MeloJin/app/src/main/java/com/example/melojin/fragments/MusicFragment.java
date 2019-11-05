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
import com.example.melojin.classes.UserConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MusicFragment extends Fragment {

    private ListView songListView;
    private SongListAdapter adapter;
    DatabaseReference databaseReference;

    public static MusicFragment newInstance() {
        MusicFragment musicFragment = new MusicFragment();

        return musicFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        UserConfig.getInstance().songList.clear();

        // adapter
        songListView = rootView.findViewById(R.id.listView);
        adapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, UserConfig.getInstance().songList);
        songListView.setAdapter(adapter);

        // read music tracks from FirebaseDatabase
        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Song value = dataSnapshot.getValue(Song.class);
                UserConfig.getInstance().songList.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
