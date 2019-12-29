package com.example.melojin;

import android.content.Context;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.melojin.activities.LoginActivity;
import com.example.melojin.activities.RegisterActivity;
import com.example.melojin.activities.SliderActivity;
import com.example.melojin.classes.Song;
import com.example.melojin.classes.UserConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.melojin", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<SliderActivity> sliderActivityTestRule = new ActivityTestRule<>(SliderActivity.class);

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    private SliderActivity sliderActivity = null;
    private LoginActivity loginActivity = null;
    private RegisterActivity registerActivity = null;

    @Before
    public void setUp() throws Exception {
        sliderActivity = sliderActivityTestRule.getActivity();
        loginActivity = loginActivityTestRule.getActivity();
        registerActivity = registerActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View sliderView = sliderActivity.findViewById(R.id.toolbar);
        View loginView = loginActivity.findViewById(R.id.editEmail);
        View registerView = registerActivity.findViewById(R.id.editNickname);

        assertNotNull(sliderView);
        assertNotNull(loginView);
        assertNotNull(registerView);
    }

    @Test
    public void testSongClass() {
        String name = "One More Time";
        String artist = "Daft Punk";
        Integer play_state = 0;
        String song_id = "0";

        Song song = new Song(name, artist, null, play_state, song_id);

        assertNotNull(song);
        assertEquals(song.getName(), name);
        assertEquals(song.getArtist(), artist);
        assertEquals(song.getPlay_state(), play_state);
        assertEquals(song.getSong_id(), song_id);
    }

    @Test
    public void testSingleton() {
        assertNotNull(UserConfig.getInstance());
    }

    @After
    public void tearDown() throws Exception {
        sliderActivity = null;
        loginActivity = null;
        registerActivity = null;
    }
}
