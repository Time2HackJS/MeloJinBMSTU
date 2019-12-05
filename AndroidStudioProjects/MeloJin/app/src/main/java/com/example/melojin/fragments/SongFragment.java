package com.example.melojin.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.melojin.R;
import com.example.melojin.classes.IPlayer;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.User;
import com.example.melojin.classes.UserConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SongFragment extends Fragment implements IPlayer {

    private TextView songName, songArtist, songCurTime, songEndTime;
    private ImageButton buttonPrev, buttonPlayPause, buttonNext, buttonDelete, buttonRepeat;
    private ImageView songPoster;
    private SeekBar seekBar;
    private Boolean playedOnClick = false;
    private Integer songLength, songPosition;
    private Boolean isPlaying = false;
    private Song prevSong;
    private Integer prevPosition = -1;
    private Boolean isRepeating = false;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    public Timer timer = new Timer();
    public int duration;
    public View rootView;

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_song, container, false);

        UserConfig.getInstance().prevSong = UserConfig.getInstance().clickedSong;

        /*---  initializing TextView elements  ---*/
        songName = rootView.findViewById(R.id.song_name_des);
        songArtist = rootView.findViewById(R.id.song_artist_des);
        songCurTime = rootView.findViewById(R.id.cur_time);
        songEndTime = rootView.findViewById(R.id.length_time);

        /*---  initializing ImageButton elements  ---*/
        buttonPrev = rootView.findViewById(R.id.skip_prev);
        buttonPlayPause = rootView.findViewById(R.id.play_pause);
        buttonNext = rootView.findViewById(R.id.skip_next);
        buttonDelete = rootView.findViewById(R.id.del_song);
        buttonRepeat = rootView.findViewById(R.id.rep_song);

        if (UserConfig.getInstance().clickedSong == UserConfig.getInstance().clickedSong && UserConfig.getInstance().clickedSong.getPlay_state() == 1) {
            isPlaying = true;
            buttonPlayPause.setImageResource(R.drawable.ic_pause);
        }

        /*---  initializing ImageView elements  ---*/
        songPoster = rootView.findViewById(R.id.songPoster);

        /*---  initializing SeekBar element  ---*/
        seekBar = rootView.findViewById(R.id.seekBar);


        /*---  setting TextView & SeekBar elements  ---*/
        songName.setText(UserConfig.getInstance().clickedSong.getName());
        songArtist.setText(UserConfig.getInstance().clickedSong.getArtist());


        if (!UserConfig.getInstance().clickedSong.equals(UserConfig.getInstance().currentSong)) {    // if this is not current song
            songCurTime.setVisibility(View.INVISIBLE);
            songEndTime.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            buttonRepeat.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.INVISIBLE);

            buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    songCurTime.setVisibility(View.VISIBLE);
                    songEndTime.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    buttonRepeat.setVisibility(View.VISIBLE);
                    buttonNext.setVisibility(View.VISIBLE);
                    buttonPrev.setVisibility(View.VISIBLE);

                    if (UserConfig.getInstance().player != null) {
                        stopSong();
                        for (Song s :  UserConfig.getInstance().savedSongList)
                            s.setPlay_state(0);
                    }

                    setOnClickEvent();
                    isPlaying = false;
                }
            });

        } else                                                                                       // if this is current song
        {
            songCurTime.setVisibility(View.VISIBLE);
            songEndTime.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            buttonRepeat.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
            buttonPrev.setVisibility(View.VISIBLE);

            buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnClickEvent();
                }
            });

            endTimeEvent();

            /*---  setting SeekBar element  ---*/

            seekbarEvent();
        }

        /*---  setting ImageView elements  ---*/
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("posters/" + UserConfig.getInstance().clickedSong.getSong_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(rootView.getContext()).load(uri).into(songPoster);
            }
        });

        /*---  setting ImageButton elements  ---*/

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent();
            }
        });

        if (UserConfig.getInstance().player != null) {
            if (UserConfig.getInstance().player.isLooping())
            {
                isRepeating = true;
                buttonRepeat.setImageResource(R.drawable.ic_loop_active);
            }
        }

        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeating) {
                    isRepeating = false;
                    UserConfig.getInstance().player.setLooping(isRepeating);
                    buttonRepeat.setImageResource(R.drawable.ic_loop);
                } else {
                    isRepeating = true;
                    UserConfig.getInstance().player.setLooping(isRepeating);
                    buttonRepeat.setImageResource(R.drawable.ic_loop_active);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserConfig.getInstance().player.seekTo(UserConfig.getInstance().player.getDuration());
            }
        });

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevClickEvent();
            }
        });

        if (UserConfig.getInstance().player != null && UserConfig.getInstance().currentSong == UserConfig.getInstance().clickedSong) {
            UserConfig.getInstance().player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ArrayList<Song> songList =  UserConfig.getInstance().savedSongList;
                    Song s = UserConfig.getInstance().currentSong;
                    int position = songList.indexOf(s);

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    if (position != songList.size() - 1) {
                        stopPlayer();
                        s.setPlay_state(0);
                        prevSong = songList.get(position + 1);

                        prevPosition = songList.indexOf(s) + 1;
                        playSong(songList.get(position + 1), position + 1, songList);
                        songList.get(position + 1).setPlay_state(1);
                        UserConfig.getInstance().adapter.notifyDataSetChanged();



                        songName.setText(prevSong.getName());
                        songArtist.setText(prevSong.getArtist());
                        isPlaying = true;

                        UserConfig.getInstance().clickedSong = prevSong;
                        UserConfig.getInstance().currentSong = prevSong;
                        UserConfig.getInstance().prevSong = s;

                        seekBar.setProgress(0);

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("posters/" + UserConfig.getInstance().clickedSong.getSong_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(rootView.getContext()).load(uri).into(songPoster);
                            }
                        });
                    } else {
                        stopPlayer();
                        s.setPlay_state(0);
                        UserConfig.getInstance().adapter.notifyDataSetChanged();

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                        getFragmentManager().popBackStack();
                    }
                }
            });
        }

        return rootView;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            duration = UserConfig.getInstance().player.getCurrentPosition();

            int durMinutes = (duration / 1000) / 60;
            int durSeconds = (duration / 1000) % 60;


            String middleThing;
            if (durSeconds < 10)
                middleThing = ":0";
            else
                middleThing = ":";

            String curTime = durMinutes + middleThing + durSeconds;

            Log.i("HOHO HAHAA", curTime);
            songCurTime.setText(curTime);

            seekBar.setProgress(duration);
        }
    }

    public void playSong(final Song s, final int position, final ArrayList<Song> songList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (UserConfig.getInstance().player == null) {

                    buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    buttonNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    buttonPrev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

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

                                        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                setOnClickEvent();
                                            }
                                        });

                                        buttonNext.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                UserConfig.getInstance().player.seekTo(UserConfig.getInstance().player.getDuration());
                                            }
                                        });

                                        buttonPrev.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                prevClickEvent();
                                            }
                                        });

                                        buttonDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteEvent();
                                            }
                                        });
                                        seekbarEvent();
                                        if (timer == null) {
                                            timer = new Timer();
                                        }
                                        endTimeEvent();

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
                                UserConfig.getInstance().adapter.notifyDataSetChanged();

                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }

                                songName.setText(prevSong.getName());
                                songArtist.setText(prevSong.getArtist());
                                isPlaying = true;

                                UserConfig.getInstance().clickedSong = prevSong;
                                UserConfig.getInstance().currentSong = prevSong;
                                UserConfig.getInstance().prevSong = s;

                                seekBar.setProgress(0);

                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                storageReference.child("posters/" + UserConfig.getInstance().clickedSong.getSong_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(rootView.getContext()).load(uri).into(songPoster);
                                    }
                                });
                            } else {
                                stopPlayer();
                                s.setPlay_state(0);
                                UserConfig.getInstance().adapter.notifyDataSetChanged();

                                if (getFragmentManager() != null)
                                {
                                    getFragmentManager().popBackStack();
                                }

                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                            }
                        }
                    });
                } else {
                    buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOnClickEvent();
                        }
                    });

                    buttonPrev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prevClickEvent();
                        }
                    });

                    buttonNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserConfig.getInstance().player.seekTo(UserConfig.getInstance().player.getDuration());
                        }
                    });

                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteEvent();
                        }
                    });

                    UserConfig.getInstance().player.start();
                }
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

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        }).start();
    }

    public void stopSong() {
        stopPlayer();
        UserConfig.getInstance().currentSong = null;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void stopPlayer() {
        if (UserConfig.getInstance().player != null) {
            UserConfig.getInstance().player.release();
            UserConfig.getInstance().player = null;
        }
        UserConfig.getInstance().currentSong = null;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setOnClickEvent() {

        if (isPlaying) {
            buttonPlayPause.setImageResource(R.drawable.ic_play_arrow);
            isPlaying = false;
            UserConfig.getInstance().player.pause();
            UserConfig.getInstance().clickedSong.setPlay_state(2);
        } else {
            buttonPlayPause.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
            playSong(UserConfig.getInstance().clickedSong,
                    UserConfig.getInstance().savedSongList.indexOf(UserConfig.getInstance().clickedSong),
                    UserConfig.getInstance().savedSongList);
            UserConfig.getInstance().clickedSong.setPlay_state(1);
        }
    }

    public void seekbarEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String betweenStr;
                    int durMinutes = (progress / 1000) / 60;
                    int durSeconds = (progress / 1000) % 60;

                    if (durSeconds < 10)
                        betweenStr = ":0";
                    else
                        betweenStr = ":";

                    songCurTime.setText(durMinutes + betweenStr + durSeconds);

                    Log.i("JAJAJA", "YES");

                    UserConfig.getInstance().player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (UserConfig.getInstance().player.isPlaying()) {
                    playedOnClick = true;
                    UserConfig.getInstance().player.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (playedOnClick)
                    UserConfig.getInstance().player.start();
            }
        });
    }

    public void endTimeEvent() {
        timer.schedule(new MyTimerTask(), 1000, 1000);

        songLength = UserConfig.getInstance().player.getDuration();
        songPosition = UserConfig.getInstance().player.getCurrentPosition();

        seekBar.setProgress(0);
        seekBar.setMax(songLength);
        seekBar.setProgress(songPosition);

        int durMinutes = (songPosition / 1000) / 60;
        int durSeconds = (songPosition / 1000) % 60;

        String middleThing;
        if (durSeconds < 10)
            middleThing = ":0";
        else
            middleThing = ":";

        songCurTime.setText(durMinutes + middleThing + durSeconds);
        Log.i("SONG CHECK", (durMinutes + middleThing + durSeconds));


        if ((songLength / 1000) % 60 < 10)
            middleThing = ":0";
        else
            middleThing = ":";

        songEndTime.setText((songLength / 1000) / 60 + middleThing + (songLength / 1000) % 60);
    }

    public void prevClickEvent() {
        ArrayList<Song> songList =  UserConfig.getInstance().savedSongList;
        Song currentSong = UserConfig.getInstance().currentSong;
        int prevSongPosition = songList.indexOf(currentSong) - 1;

        if (prevSongPosition != -1)
        {
            Song prevSong = songList.get(prevSongPosition);
            stopSong();
            playSong(prevSong, prevSongPosition, songList);

            UserConfig.getInstance().prevSong = prevSong;

            UserConfig.getInstance().clickedSong = prevSong;
            UserConfig.getInstance().currentSong = prevSong;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("posters/" + UserConfig.getInstance().clickedSong.getSong_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(rootView.getContext()).load(uri).into(songPoster);
                }
            });

            songName.setText(prevSong.getName());
            songArtist.setText(prevSong.getArtist());

            currentSong.setPlay_state(0);
            prevSong.setPlay_state(1);

            isPlaying = true;
            buttonPlayPause.setImageResource(R.drawable.ic_pause);

            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            buttonRepeat.setImageResource(R.drawable.ic_loop);

            seekbarEvent();
        }
    }

    public void deleteEvent() {
        Song song = UserConfig.getInstance().clickedSong;

        if (UserConfig.getInstance().player != null) {
            stopSong();
            stopPlayer();
        }

        UserConfig.getInstance().savedSongList.remove(song);
        UserConfig.getInstance().currentUser.songs.remove(song.getSong_id());

        FirebaseDatabase.getInstance().getReference("users")
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .child("songs")
                .setValue(UserConfig.getInstance().currentUser.songs);

        Toast.makeText(getContext(), "You successfuly deleted " + song.getName() + " from your playlist!", Toast.LENGTH_SHORT).show();
        UserConfig.getInstance().adapter.notifyDataSetChanged();

        getFragmentManager().popBackStack();
    }


}
