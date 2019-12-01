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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;

import com.example.melojin.R;
import com.example.melojin.classes.IPlayer;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.SongListAdapter;
import com.example.melojin.classes.UserConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class MusicFragment extends Fragment implements IPlayer {

    private Integer prevPosition = -1;
    private ImageView prevImageView;
    private LinearLayout prevLayout;
    private Song prevSong;
    private EditText etSearch;

    private ListView songListView;
    private SongListAdapter adapter;
    private SongListAdapter searchAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

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

                setOnClickEvent(parent, view, position, id);
            }
        });

        songListView.setLongClickable(true);
        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                UserConfig.getInstance().clickedSong = UserConfig.getInstance().songList.get(position);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new SongFragment()).addToBackStack(null).commit();

                return true;
            }
        });

        return rootView;
    }

    public void playSong(final Song s, final int position, final ArrayList<Song> songList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });

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
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(mFirebaseAuth.getCurrentUser().getUid())
                                                .child("current_song")
                                                .setValue(s.getArtist() + " - " + s.getName());

                                        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                setOnClickEvent(adapterView, view, i, l);
                                            }
                                        });
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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        if (searchAdapter != null) searchAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            setOnClickEvent(adapterView, view, i, l);
                        }
                    });
                    UserConfig.getInstance().player.start();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (searchAdapter != null) searchAdapter.notifyDataSetChanged();
                    }
                });

                UserConfig.getInstance().currentSong = s;
            }
        }).start();
    }

    public void pauseSong() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (UserConfig.getInstance().player != null) {
                    UserConfig.getInstance().player.pause();
                }
            }
        }).start();

    }

    public void stopSong() {
        stopPlayer();
        UserConfig.getInstance().currentSong = null;
    }

    public void stopPlayer() {
        if (UserConfig.getInstance().player != null) {
            UserConfig.getInstance().player.release();
            UserConfig.getInstance().player = null;
        }
        UserConfig.getInstance().currentSong = null;
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

    public void setOnClickEvent(AdapterView<?> parent, View view, int position, long id) {
        Song selectedSong = (Song) parent.getItemAtPosition(position);
        UserConfig.getInstance().currentSong = selectedSong;

        if (selectedSong.getPlay_state() == 0) {

            stopSong();
            if (etSearch.getText().toString().isEmpty())
                playSong(selectedSong, position, UserConfig.getInstance().songList);
            else
                playSong(selectedSong, position, filterList(etSearch.getText().toString()));
            selectedSong.setPlay_state(1);

        } else if (selectedSong.getPlay_state() == 2) {
            if (etSearch.getText().toString().isEmpty())
                playSong(selectedSong, position, UserConfig.getInstance().songList);
            else

                playSong(selectedSong, position, filterList(etSearch.getText().toString()));
            selectedSong.setPlay_state(1);

        } else {

            pauseSong();
            selectedSong.setPlay_state(2);

        }

        if (UserConfig.getInstance().prevPosition == null) UserConfig.getInstance().prevPosition = position;

        if (UserConfig.getInstance().prevPosition != position) {
            UserConfig.getInstance().prevSong.setPlay_state(0);
            UserConfig.getInstance().songList.get(UserConfig.getInstance().prevPosition).setPlay_state(0);
        }

        UserConfig.getInstance().prevPosition = position;
        UserConfig.getInstance().prevSong = selectedSong;
        adapter.notifyDataSetChanged();
        if (searchAdapter != null) searchAdapter.notifyDataSetChanged();
    }
}

