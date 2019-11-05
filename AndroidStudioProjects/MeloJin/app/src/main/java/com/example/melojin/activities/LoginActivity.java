package com.example.melojin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.melojin.R;

import com.example.melojin.classes.UserConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //
    // initializing activity elements
    //

    EditText emailET;
    EditText passwordET;
    Button btnLogin;
    TextView tvRegister;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MJ: LoginActivity";

    // overriding back button to prevent unlogged using of app
    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "ACTIVITY STARTED");
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //
        // initializing activity elements
        //

        emailET = findViewById(R.id.editEmail);
        passwordET = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.createText);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //
        // getting user nickname from firebase database
        //

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {

                    DatabaseReference user = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid());
                        user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);

                                UserConfig.getInstance().userName = name;
                                UserConfig.getInstance().userEmail = email;

                                Log.i(TAG, "GOT " + name + " AND " + email);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                    Intent i = new Intent(LoginActivity.this, SliderActivity.class);
                    startActivity(i);
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if (email.isEmpty()) {
                    emailET.setError("Please, enter E-Mail!");
                    emailET.requestFocus();
                }
                else if (password.isEmpty()) {
                    passwordET.setError("Please, enter password!");
                    passwordET.requestFocus();
                }
                else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && password.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, SliderActivity.class));
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Log in error, please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intRegister);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
