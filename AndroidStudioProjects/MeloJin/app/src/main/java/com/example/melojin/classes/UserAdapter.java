package com.example.melojin.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melojin.R;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    private Context mContext;
    int mResource;

    public UserAdapter(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).name;
        String email = getItem(position).email;
        String curSong = getItem(position).current_song;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.user_nickname);
        TextView tvEmail = convertView.findViewById(R.id.user_email);
        TextView tvCurSong = convertView.findViewById(R.id.user_currentsong);
        ImageView ivAvatar = convertView.findViewById(R.id.user_avatar);

        if (curSong.equals(""))
            convertView.findViewById(R.id.nowlistening).setVisibility(View.INVISIBLE);
        else
            convertView.findViewById(R.id.nowlistening).setVisibility(View.VISIBLE);

        tvName.setText(name);
        tvEmail.setText(email);
        tvCurSong.setText(curSong);

        ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);

        return convertView;
    }
}
