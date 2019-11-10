package com.example.melojin.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.ArrayList;

public class MusicFragment extends Fragment {

    private Integer prevPosition = -1;
    private ImageView prevImageView;
    private LinearLayout prevLayout;
    private Song prevSong;
    private EditText etSearch;

    private ListView songListView;
    private SongListAdapter adapter;
    private SongListAdapter searchAdapter;
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

        if (UserConfig.getInstance().searchString == null) {
            songListView.setAdapter(adapter);
        }
        else {
            searchAdapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, filterList(UserConfig.getInstance().searchString));
            songListView.setAdapter(searchAdapter);
        }

        etSearch = rootView.findViewById(R.id.fieldSearch);
        etSearch.setText(UserConfig.getInstance().searchString);
        //adapter.notifyDataSetChanged();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etSearch.getText().toString().isEmpty())
                {
                    songListView.setAdapter(adapter);
                    //for (Song v : UserConfig.getInstance().songList) v.setPlay_state(0);
                }
                else {
                    UserConfig.getInstance().searchString = etSearch.getText().toString();
                    searchAdapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, filterList(UserConfig.getInstance().searchString));
                    songListView.setAdapter(searchAdapter);
                }
            }
        });

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

                if (selectedSong.getPlay_state() == 0) {
                    stopSong();
                    if (etSearch.getText().toString().isEmpty())
                        playSong(selectedSong, position, UserConfig.getInstance().songList);
                    else
                        playSong(selectedSong, position, filterList(etSearch.getText().toString()));
                    selectedSong.setPlay_state(1);
                    SystemClock.sleep(1000);
                    /*
                    selectedSong.setPlay_state(1);
                    ivState.setImageResource(R.drawable.song_pause);
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
                     */
                } else if (selectedSong.getPlay_state() == 2) {
                    if (etSearch.getText().toString().isEmpty())
                        playSong(selectedSong, position, UserConfig.getInstance().songList);
                    else
                        playSong(selectedSong, position, filterList(etSearch.getText().toString()));
                    selectedSong.setPlay_state(1);
                    /*
                    ivState.setImageResource(R.drawable.song_pause);
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
                    */
                } else {
                    pauseSong();
                    selectedSong.setPlay_state(2);
                    /*
                    selectedSong.setPlay_state(2);
                    ivState.setImageResource(R.drawable.song_play);
                     */
                }

                if (UserConfig.getInstance().prevPosition == null) UserConfig.getInstance().prevPosition = position;

                if (UserConfig.getInstance().prevPosition != position) {
                    /*
                    if (prevPosition == UserConfig.getInstance().prevPosition) {
                        prevImageView = UserConfig.getInstance().preView.findViewById(R.id.song_button);
                        prevLayout = UserConfig.getInstance().preView.findViewById(R.id.song_layout);
                    }

                    prevImageView.setImageResource(0);
                    prevLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.zalupa));
                    */
                    UserConfig.getInstance().prevSong.setPlay_state(0);
                    UserConfig.getInstance().songList.get(UserConfig.getInstance().prevPosition).setPlay_state(0);
                }

                UserConfig.getInstance().prevPosition = position;
                UserConfig.getInstance().prevSong = selectedSong;
                adapter.notifyDataSetChanged();
                searchAdapter.notifyDataSetChanged();

            }
        });

        return rootView;
    }

    private void playSong(final Song s, final int position, final ArrayList<Song> songList) {
        if (UserConfig.getInstance().player == null) {
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

            UserConfig.getInstance().player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (position != songList.size() - 1) {
                        stopPlayer();
                        s.setPlay_state(0);
                        prevSong = songList.get(position + 1);

                        prevPosition = songList.indexOf(s) + 1;
                        playSong(songList.get(position + 1), position + 1, songList);
                        songList.get(position + 1).setPlay_state(1);
                        //adapter.notifyDataSetChanged();
                    } else
                    {
                        stopPlayer();
                        s.setPlay_state(0);
                        //adapter.notifyDataSetChanged();
                    }
                }
            });
        } else
            UserConfig.getInstance().player.start();
        searchAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
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

    public ArrayList<Song> filterList(String str) {
        ArrayList<Song> filteredList = new ArrayList<Song>();
        String searchString;
        for (Song v : UserConfig.getInstance().songList) {
            searchString = v.getArtist() + " " + v.getName();
            if (searchString.toLowerCase().contains(str.toLowerCase())) filteredList.add(v);
        }

        return filteredList;
    }
}
