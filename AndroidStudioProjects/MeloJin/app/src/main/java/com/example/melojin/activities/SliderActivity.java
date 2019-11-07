package com.example.melojin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melojin.classes.Song;
import com.example.melojin.classes.UserConfig;
import com.example.melojin.fragments.MusicFragment;
import com.example.melojin.fragments.ProfileFragment;

import com.example.melojin.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SliderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    DatabaseReference databaseReference;
    private static final String TAG = "MJ: SliderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "ACTIVITY STARTED");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        // add Nickname and Email to UserConfig
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // get nickname and email from firebase
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid());

        // thread started
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);

                UserConfig.getInstance().userName = name;
                UserConfig.getInstance().userEmail = email;

                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = headerView.findViewById(R.id.navUsername);
                TextView navEmail = headerView.findViewById(R.id.navEmail);
                navUsername.setText(UserConfig.getInstance().userName);
                navEmail.setText(UserConfig.getInstance().userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // change header Email and Nickname
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MusicFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_music);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // handling navigation view item clicks
        switch (item.getItemId()) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

            case R.id.nav_music:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MusicFragment()).commit();
                break;

            case R.id.nav_friends:

                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Successfuly logged out!", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intToLogin = new Intent(SliderActivity.this, LoginActivity.class);
                startActivity(intToLogin);
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
