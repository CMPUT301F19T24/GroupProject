package com.example.groupproject.ui.relations;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.groupproject.R;
import com.example.groupproject.User;


import java.util.List;

public class CustomUserList extends ArrayAdapter<User> {
    /*
    Custom view of a user list
     */
    private List<User> users;
    private Context context;

    public CustomUserList(Context context, List<User> users){
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    // Specify which view the user list will use: In this case Following/Requests
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        User user = this.users.get(position);

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.v_user_list_layout, parent, false);
        }

        Button personName = view.findViewById(R.id.user_list_person_name);
        // Darken every other item on the list for easier viewing
        if (position % 2 == 1){
            personName.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
        }

        personName.setText(user.getUserName());

        return view;
    }
}
