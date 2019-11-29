package com.example.groupproject.ui.relations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;

import java.util.List;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;

public class ListViewUsersFollowingMe extends ArrayAdapter<Relationship> {
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek, Donald
     *
     */
    private List<Relationship> relations;
    private Context context;


    public ListViewUsersFollowingMe(Context context, List<Relationship> relations){
        /**
         * @param context
         * @param users List of users to be displayed
         */
        super(context, 0, relations);
        this.relations = relations;
        this.context = context;
    }

    @NonNull
    @Override
    // Specify which view the user list will use: In this case Following/Requests
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        /**
         * Display a list of people who are allowed to follow you
         *
         * @param position Item in user list to be displayed to view
         * @param convertView Previously existing view, Nullable
         * @return view View item modelled after position of list.
         */
        View view = convertView;
        final Relationship relationship = this.relations.get(position);
        final String sender = relationship.getSender().getUserName();

        final String receiver = relationship.getRecipiant().getUserName();
        final RelationshipStatus rs = relationship.getStatus();

        view = LayoutInflater.from(context).inflate(R.layout.e_users_following_me, parent, false);
        TextView username = view.findViewById(R.id.tv_users_following_me_username);

        username.setText(sender);

        return view;
    }
}
