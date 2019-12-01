package com.example.melojin.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.melojin.R;
import com.example.melojin.classes.UserConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Timer;
import java.util.TimerTask;

public class SongFragment extends Fragment {

    private TextView songName, songArtist, songCurTime, songEndTime;
    private ImageButton buttonPrev, buttonPlayPause, buttonNext;
    private ImageView songPoster;
    private SeekBar seekBar;
    private Boolean playedOnClick = false;
    private Integer songLength, songPosition;
    private LinearLayout songLinear;

    public Timer timer = new Timer();
    public int duration;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
        timer = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_song, container, false);

        /*---  initializing TextView elements  ---*/
        songName = rootView.findViewById(R.id.song_name_des);
        songArtist = rootView.findViewById(R.id.song_artist_des);
        songCurTime = rootView.findViewById(R.id.cur_time);
        songEndTime = rootView.findViewById(R.id.length_time);

        /*---  initializing ImageButton elements  ---*/
        buttonPrev = rootView.findViewById(R.id.skip_prev);
        buttonPlayPause = rootView.findViewById(R.id.play_pause);
        buttonNext = rootView.findViewById(R.id.skip_next);

        /*---  initializing ImageView elements  ---*/
        songPoster = rootView.findViewById(R.id.songPoster);

        /*---  initializing SeekBar element  ---*/
        seekBar = rootView.findViewById(R.id.seekBar);


        /*---  setting TextView & SeekBar elements  ---*/
        songName.setText(UserConfig.getInstance().clickedSong.getName());
        songArtist.setText(UserConfig.getInstance().clickedSong.getArtist());


        if (!UserConfig.getInstance().clickedSong.equals(UserConfig.getInstance().currentSong)) {
            songCurTime.setVisibility(View.INVISIBLE);
            songEndTime.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
        } else
        {
            songCurTime.setVisibility(View.VISIBLE);
            songEndTime.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);

            timer.schedule(new MyTimerTask(), 1000, 1000);

            songLength = UserConfig.getInstance().player.getDuration();
            songPosition = UserConfig.getInstance().player.getCurrentPosition();

            int durMinutes = (songPosition / 1000) / 60;
            int durSeconds = (songPosition / 1000) % 60;

            String middleThing;
            if (durSeconds < 10)
                middleThing = ":0";
            else
                middleThing = ":";

            songCurTime.setText(durMinutes + middleThing + durSeconds);
            Log.i("SONG CHECK", (durMinutes + middleThing + durSeconds));


            songEndTime.setText((songLength / 1000) / 60 + ":" + (songLength / 1000) % 60);

            /*---  setting SeekBar element  ---*/
            seekBar.setProgress(0);
            seekBar.setMax(songLength);
            seekBar.setProgress(songPosition);
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
                    if (UserConfig.getInstance().player.isPlaying())
                    {
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

        /*---  setting ImageView elements  ---*/
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("posters/" + UserConfig.getInstance().clickedSong.getSong_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(rootView.getContext()).load(uri).into(songPoster);
            }
        });

        return rootView;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
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
            });
        }
    }
}
