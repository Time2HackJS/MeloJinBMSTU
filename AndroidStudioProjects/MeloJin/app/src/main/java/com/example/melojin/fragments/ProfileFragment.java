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

        profileNickname = rootView.findViewById(R.id.userNickname);
        profileNickname.setText(UserConfig.getInstance().userName);

        return rootView;
    }
}
