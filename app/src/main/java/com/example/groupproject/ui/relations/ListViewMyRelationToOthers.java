package com.example.groupproject.ui.relations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;

import java.util.List;

public class ListViewMyRelationToOthers extends ArrayAdapter<Relationship> {
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek, Donald
     *
     */
    private List<Relationship> relations;
    private Context context;


    public ListViewMyRelationToOthers(Context context, List<Relationship> relations){
        /**
         * @param context
         * @param users List of users to be displayed
         */
        super(context, 0, relations);
        this.context = context;
        this.relations = relations;
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

        final Relationship rs = this.relations.get(position);
        final String username = rs.getSender().getUserName();
        final String status = rs.getStatus().toString();
        final RelationshipStatus rss = rs.getStatus();

        view = LayoutInflater.from(context).inflate(R.layout.e_my_relation_to_others, parent, false);
        final TextView tv_username = view.findViewById(R.id.e_tv_my_relation_to_others_username);
        final TextView tv_status = view.findViewById(R.id.e_tv_my_relation_to_others_status);

        tv_username.setText(username);
        tv_status.setText(status);

        final Button b_back = view.findViewById(R.id.b_relationship_back);
        final Button b_forward = view.findViewById(R.id.b_relationship_forward);


        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rss == RelationshipStatus.INVISIBLE)
                {
                    // NONE
                }

                else if(rss == RelationshipStatus.PENDING)
                {
                    rs.setStatus(RelationshipStatus.INVISIBLE);

                    b_back.setVisibility(View.GONE);

                    b_forward.setVisibility(View.VISIBLE);
                    b_forward.setText("Request Permission");

                    // TODO UPDATE REMOTE
                }

                else if(rss == RelationshipStatus.FOLLOWING)
                {
                    rs.setStatus(RelationshipStatus.INVISIBLE);

                    b_back.setVisibility(View.VISIBLE);
                    b_back.setText("Request Permission");

                    b_forward.setVisibility(View.GONE);

                    // TODO UPDATE REMOTE
                }

                else{}
            }
        });

        b_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rss == RelationshipStatus.INVISIBLE)
                {
                    rs.setStatus(RelationshipStatus.PENDING);

                    b_back.setVisibility(View.VISIBLE);
                    b_back.setText("Cancel Request");

                    b_forward.setVisibility(View.GONE);


                    // TODO UPDATE REMOTE
                }

                else if(rss == RelationshipStatus.PENDING)
                {
                    // NONE
                }

                else if(rss == RelationshipStatus.FOLLOWING)
                {
                    // NONE
                }

                else{}
            }
        });
        return view;
    }
}
