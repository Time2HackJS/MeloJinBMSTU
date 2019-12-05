package com.example.melojin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.melojin.R;
import com.example.melojin.activities.LoginActivity;
import com.example.melojin.activities.UsersActivity;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.User;
import com.example.melojin.classes.UserAdapter;
import com.example.melojin.classes.UserConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersFragment extends Fragment {

    private ListView userListView;
    private UserAdapter adapter;
    DatabaseReference databaseReference;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_users, container, false);

        userListView = rootView.findViewById(R.id.userListView);
        adapter = new UserAdapter(getActivity(), R.layout.users_adapter, UserConfig.getInstance().users);

        userListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User clickedUser = (User) parent.getItemAtPosition(position);

                if (!UserConfig.getInstance().currentUser.friendList.contains(clickedUser)) {
                    UserConfig.getInstance().currentUser.friendList.add(clickedUser);
                    UserConfig.getInstance().currentUser.friends.add(clickedUser.email);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(mFirebaseAuth.getCurrentUser().getUid())
                            .child("friends")
                            .setValue(UserConfig.getInstance().currentUser.friends);

                    Toast.makeText(getContext(), "You successfuly followed " + clickedUser.name + "!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserConfig.getInstance().clickedUser = (User) adapterView.getItemAtPosition(i);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new ProfileFragment()).addToBackStack(null).commit();

                return true;
            }
        });

        return rootView;
    }
}
