package com.example.melojin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.melojin.R;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.SongListAdapter;
import com.example.melojin.classes.UserConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MusicGlobalFragment extends Fragment {

    private ListView songListView;
    private SongListAdapter adapter;
    private SongListAdapter searchAdapter;
    private EditText etSearch;
    private FirebaseAuth mFirebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_main, container, false);

        songListView = rootView.findViewById(R.id.listView);
        adapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, UserConfig.getInstance().songList);

        if (UserConfig.getInstance().searchString == null) {
            songListView.setAdapter(adapter);
            UserConfig.getInstance().adapter = adapter;
        }
        else {
            searchAdapter = new SongListAdapter(getActivity(), R.layout.adapter_view_layout, filterList(UserConfig.getInstance().searchString));
            songListView.setAdapter(searchAdapter);
            UserConfig.getInstance().adapter = searchAdapter;
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
                    UserConfig.getInstance().adapter = searchAdapter;
                }
            }
        });

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song clickedSong = (Song) parent.getItemAtPosition(position);
                if (!UserConfig.getInstance().currentUser.songs.contains(clickedSong.getSong_id()))
                {
                    UserConfig.getInstance().savedSongList.add(clickedSong);
                    UserConfig.getInstance().currentUser.songs.add(clickedSong.getSong_id());
                    Toast.makeText(getContext(), "You successfuly added " + clickedSong.getName() + " to your playlist!", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(mFirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("songs").setValue(UserConfig.getInstance().currentUser.songs);
                }
            }
        });

        adapter.notifyDataSetChanged();
        return rootView;
    }

    public ArrayList<Song> filterList(String str) {
        ArrayList<Song> filteredList = new ArrayList<Song>();
        String searchString;
        for (Song v :  UserConfig.getInstance().songList) {
            searchString = v.getArtist() + " " + v.getName();
            if (searchString.toLowerCase().contains(str.toLowerCase())) filteredList.add(v);
        }

        return filteredList;
    }
}

