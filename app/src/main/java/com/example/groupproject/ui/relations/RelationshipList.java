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

public class RelationshipList extends ArrayAdapter<Relationship> {
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek, Donald
     *
     */
    private List<Relationship> relations;
    private Context context;


    public RelationshipList(Context context, List<Relationship> relations){
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
         * Builds a view with respect to user in a list.
         * @param position Item in user list to be displayed to view
         * @param convertView Previously existing view, Nullable
         * @return view View item modelled after position of list.
         */
        View view = convertView;
        final Relationship relationship = this.relations.get(position);
        final String sender = relationship.getSender().getUserName();
        final String receiver = relationship.getRecipiant().getUserName();
        final RelationshipStatus rs = relationship.getStatus();

        view = LayoutInflater.from(context).inflate(R.layout.e_list_relationship, parent, false);
        TextView username = view.findViewById(R.id.e_tv_relationship_username);
        TextView status = view.findViewById(R.id.e_tv_relationship_status);

        username.setText(receiver);

        status.setText(rs.toString());

        status.setClickable(true);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), rs.getDesc(), Toast.LENGTH_SHORT).show();

            }
        });

        Button b_back = view.findViewById(R.id.b_relationship_back);
        Button b_forward = view.findViewById(R.id.b_relationship_forward);

        // DO NOT USE A SWITCH CASE
        if(rs == RelationshipStatus.INVISIBLE)
        {
            b_back.setVisibility(View.GONE);
            b_forward.setText("Request Permission to View Mood Events");

            b_forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relationship.setStatus(RelationshipStatus.PENDING);
                    FSH_INSTANCE.getInstance().fsh.editRelationship(relationship);
                    notifyDataSetChanged();
                }
            });
        }
        else if(rs == RelationshipStatus.PENDING)
        {
            b_back.setText("Cancel");
            b_forward.setVisibility(View.GONE);

            b_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relationship.setStatus(RelationshipStatus.INVISIBLE);
                    FSH_INSTANCE.getInstance().fsh.editRelationship(relationship);
                    notifyDataSetChanged();
                }
            });
        }
        else if(rs == RelationshipStatus.FOLLOWING)
        {
            b_back.setText("Hide Posts");
            b_forward.setText("Follow");

            b_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relationship.setStatus(RelationshipStatus.INVISIBLE);
                    FSH_INSTANCE.getInstance().fsh.editRelationship(relationship);                    notifyDataSetChanged();
                }
            });
            b_forward.setVisibility(View.GONE);

        }
        return view;
    }
}
