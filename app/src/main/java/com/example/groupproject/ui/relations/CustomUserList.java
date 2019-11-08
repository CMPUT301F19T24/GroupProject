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
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek
     *
     */
    private List<User> users;
    private Context context;

    public CustomUserList(Context context, List<User> users){
        /**
         * @param context
         * @param users List of users to be displayed
         */
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    // Specify which view the user list will use: In this case Following/Requests
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        /**
         * Builds a view with respect to user in a list.
         * @param position Item in user list to be displayed to view
         * @param convertView Previously existing view, Nullable
         * @return view View item modelled after position of list.
         */
        View view = convertView;
        User user = this.users.get(position);

        view = LayoutInflater.from(context).inflate(R.layout.v_user_list_layout, parent, false);


        TextView personName = view.findViewById(R.id.user_list_person_name);
        // Darken every other item on the list for easier viewing
        if (position % 2 == 1){
            personName.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
        }

        personName.setText(user.getUserName());

        return view;
    }
}
