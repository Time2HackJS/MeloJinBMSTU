package com.example.melojin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melojin.R;
import com.example.melojin.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText emailET, passwordET, nicknameET;
    Button btnRegister;
    TextView tvLogin;
    FirebaseAuth mFirebaseAuth;

    private static final String TAG = "MJ: RegisterActivity";

    @Override
    public void onBackPressed(){}

    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth.getCurrentUser() != null) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.editEmail);
        passwordET = findViewById(R.id.editPassword);
        nicknameET = findViewById(R.id.editNickname);
        btnRegister = findViewById(R.id.buttonRegister);
        tvLogin = findViewById(R.id.loginText);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailET.getText().toString();
                final String password = passwordET.getText().toString();
                final String nickname = nicknameET.getText().toString();

                if (email.isEmpty() && password.isEmpty() && nickname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                } else if (nickname.isEmpty()) {
                    passwordET.setError("Please, enter nickname!");
                    passwordET.requestFocus();
                } else if (email.isEmpty()) {
                    emailET.setError("Please, enter E-Mail!");
                    emailET.requestFocus();
                } else if (password.isEmpty()) {
                    passwordET.setError("Please, enter password!");
                    passwordET.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty() && nickname.isEmpty())) {

                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<String> friends = new ArrayList<>();
                                        friends.add("pupa");
                                        friends.add("lupa");

                                        ArrayList<String> savedSongs = new ArrayList<>();
                                        savedSongs.add("1");
                                        savedSongs.add("3");
                                        savedSongs.add("4");

                                        User user = new User(nickname, email, friends, savedSongs, "");
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(mFirebaseAuth.getCurrentUser().getUid())
                                                .setValue(user);

                                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, SliderActivity.class));
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration unsuccessful, please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
