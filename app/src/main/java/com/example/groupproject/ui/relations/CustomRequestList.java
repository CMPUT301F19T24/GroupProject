package com.example.groupproject.ui.relations;

import android.content.Context;
import android.graphics.Color;
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

public class CustomRequestList extends ArrayAdapter<User>{
    /*
    Custom view of a user list
     */
    private List<User> users;
    private Context context;

    public CustomRequestList(Context context, List<User> users){
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
//        view.setBackgroundColor(getRe);
        view = LayoutInflater.from(context).inflate(R.layout.v_requests, parent, false);

        TextView personName = view.findViewById(R.id.request_person_name);

        personName.setText(user.getUserName());

        return view;
    }
}
