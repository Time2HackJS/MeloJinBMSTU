package com.example.melojin;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;



public class SongListAdapter extends ArrayAdapter<Song> {

    private static final String TAG = "SongListAdapter";

    private Context mContext;
    int mResource;

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

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

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.song_name);
        TextView tvArtist = convertView.findViewById(R.id.song_artist);
        ImageView ivSource = convertView.findViewById(R.id.song_image);
        ImageView ivState = convertView.findViewById(R.id.song_button);
        LinearLayout layout = convertView.findViewById(R.id.song_layout);

        ivSource.setImageResource(getImageId(mContext, pic_folder));

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

        return convertView;
    }
}
