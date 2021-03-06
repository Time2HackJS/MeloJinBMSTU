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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private TextView profileNickname;
    private TextView textView1, textView2;
    private DatabaseReference databaseReference;
    private String nowListening;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);

        // set avatar
        avatarImageView = rootView.findViewById(R.id.avatarImage);
        avatarImageView.setImageResource(R.drawable.avatar_sample);

        textView1 = rootView.findViewById(R.id.textView1);
        textView2 = rootView.findViewById(R.id.textView2);

        if (UserConfig.getInstance().clickedUser.current_song != null) {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);

            textView2.setText(UserConfig.getInstance().clickedUser.current_song);
        } else
        {
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
        }

        profileNickname = rootView.findViewById(R.id.userNickname);
        profileNickname.setText(UserConfig.getInstance().clickedUser.name);

        return rootView;
    }
}
