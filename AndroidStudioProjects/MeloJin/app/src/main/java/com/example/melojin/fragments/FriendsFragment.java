package com.example.melojin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.melojin.R;
import com.example.melojin.classes.User;
import com.example.melojin.classes.UserAdapter;
import com.example.melojin.classes.UserConfig;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private ArrayList<User> usersList, friendsList;
    private ArrayList<String> friendsString;
    private ListView userListView;
    private UserAdapter adapter;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_users, container, false);
        usersList = UserConfig.getInstance().users;
        friendsString = UserConfig.getInstance().currentUser.friends;



        userListView = rootView.findViewById(R.id.userListView);
        adapter = new UserAdapter(getActivity(), R.layout.users_adapter, UserConfig.getInstance().currentUser.friendList);

        userListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Log.i("FRIENDSCHECK", UserConfig.getInstance().currentUser.friendList.get(0).email);

        return rootView;
    }
}
