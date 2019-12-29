package com.example.melojin.classes;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.melojin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SongListAdapter extends ArrayAdapter<Song> {

    private static final String TAG = "MJ: SongListAdapter";

    private Context mContext;
    int mResource;

    public SongListAdapter(Context context, int resource, ArrayList<Song> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the songs information
        String name = getItem(position).getName();
        String artist = getItem(position).getArtist();
        String pic_folder = getItem(position).getPic_folder();
        Integer song_state = getItem(position).getPlay_state();
        String song_id = getItem(position).getSong_id();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.song_name);
        TextView tvArtist = convertView.findViewById(R.id.song_artist);
        final ImageView ivSource = convertView.findViewById(R.id.song_image);
        ImageView ivState = convertView.findViewById(R.id.song_button);
        LinearLayout layout = convertView.findViewById(R.id.song_layout);

        tvName.setSelected(true);

        if (song_state != 0) {
            UserConfig.getInstance().prevPosition = position;
            UserConfig.getInstance().preView = convertView;
        }

        // get image from Firebase storage

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("posters_small/" + song_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).into(ivSource);
            }
        });

        if (song_state == 1) {
            ivState.setImageResource(R.drawable.song_pause);
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
        }
        else if (song_state == 2) {
            ivState.setImageResource(R.drawable.song_play);
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.songSelectBackground));
        }
        else ivState.setImageResource(0);


        tvName.setText(name);
        tvArtist.setText(artist);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        convertView.startAnimation(animation);

        Log.i("CHECK", UserConfig.getInstance().songList.get(0).getPlay_state().toString());

        return convertView;
    }
}
