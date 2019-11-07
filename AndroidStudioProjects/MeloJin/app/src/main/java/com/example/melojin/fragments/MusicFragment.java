package com.example.melojin.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.melojin.R;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.SongListAdapter;
import com.example.melojin.classes.UserConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MusicFragment extends Fragment {

    private Integer prevPosition = -1;
    private ImageView prevImageView;
    private LinearLayout prevLayout;
    private Song prevSong;

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
        final View rootView = inflater.inflate(R.layout.activity_main, container, false);
        //UserConfig.getInstance().songList.clear();
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.main_background);

        // adapter
        songListView = rootView.findViewById(R.id.listView);
        adapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, UserConfig.getInstance().songList);
        songListView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        if (UserConfig.getInstance().songList.isEmpty()) {
            Log.i("FUCK", "YAY");
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
         }

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the selected item
                Song selectedSong = (Song) parent.getItemAtPosition(position);
                UserConfig.getInstance().currentSong = selectedSong;

                ImageView ivState = view.findViewById(R.id.song_button);
                LinearLayout layout = view.findViewById(R.id.song_layout);

                if (selectedSong.getPlay_state() == 0) {
                    stopSong();
                    playSong(selectedSong);
                    selectedSong.setPlay_state(1);
                    ivState.setImageResource(R.drawable.song_pause);
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
                } else if (selectedSong.getPlay_state() == 2) {
                    playSong(selectedSong);
                    selectedSong.setPlay_state(1);
                    ivState.setImageResource(R.drawable.song_pause);
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
                } else {
                    pauseSong();
                    selectedSong.setPlay_state(2);
                    ivState.setImageResource(R.drawable.song_play);
                }

                if (prevPosition == -1) prevPosition = position;

                if (prevPosition != position) {
                    if (prevPosition == UserConfig.getInstance().prevPosition) {
                        prevImageView = UserConfig.getInstance().preView.findViewById(R.id.song_button);
                        prevLayout = UserConfig.getInstance().preView.findViewById(R.id.song_layout);
                    }

                    prevImageView.setImageResource(0);
                    prevLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.zalupa));
                    prevSong.setPlay_state(0);
                }

                prevPosition = position;
                prevLayout = layout;
                prevImageView = ivState;
                prevSong = selectedSong;
                //adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    private void playSong(Song s) {
        UserConfig.getInstance().player = new MediaPlayer();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("songs/song_" + s.getSong_id() + ".mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    UserConfig.getInstance().player.setDataSource(uri.toString());

                    UserConfig.getInstance().player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });

                    UserConfig.getInstance().player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void pauseSong() {
        if (UserConfig.getInstance().player != null) {
            UserConfig.getInstance().player.pause();
        }
    }

    public void stopSong() {
        stopPlayer();
    }

    private void stopPlayer() {
        if (UserConfig.getInstance().player != null) {
            UserConfig.getInstance().player.release();
            UserConfig.getInstance().player = null;
        }
    }
}
