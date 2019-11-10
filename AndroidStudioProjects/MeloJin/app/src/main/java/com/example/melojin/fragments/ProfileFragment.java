package com.example.melojin.fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.melojin.R;
import com.example.melojin.classes.UserConfig;

public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private TextView profileNickname;
    private TextView textView1, textView2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);

        if (UserConfig.getInstance().songList.isEmpty()) {
            Log.i("FUCK", "HUH");
        }

        // set avatar
        avatarImageView = rootView.findViewById(R.id.avatarImage);
        avatarImageView.setImageResource(R.drawable.avatar_sample);

        textView1 = rootView.findViewById(R.id.textView1);
        textView2 = rootView.findViewById(R.id.textView2);

        if (UserConfig.getInstance().currentSong != null) {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);

            String songName = UserConfig.getInstance().currentSong.getName();
            String songArtist = UserConfig.getInstance().currentSong.getArtist();

            textView2.setText(songArtist + " - " + songName);
        } else
        {
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
        }

        profileNickname = rootView.findViewById(R.id.userNickname);
        profileNickname.setText(UserConfig.getInstance().userName);

        return rootView;
    }
}
