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

public class ResponsesList extends ArrayAdapter<Relationship> {
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek
     *
     */
    private List<Relationship> relations;
    private Context context;


    public ResponsesList(Context context, List<Relationship> relations){
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

        if(!relations.isEmpty()) {
            view = LayoutInflater.from(context).inflate(R.layout.e_list_responses, parent, false);
            TextView username = view.findViewById(R.id.e_tv_responses_username);
            final TextView status = view.findViewById(R.id.e_tv_responses_status);

            username.setText(sender);
            status.setClickable(true);

            final Button b_accept = view.findViewById(R.id.b_responses_accept);
            final Button b_decline = view.findViewById(R.id.b_responses_decline);

            // DO NOT USE A SWITCH CASE
            if (rs == RelationshipStatus.PENDING_VISIBLE) {
                status.setText("Requesting to view");
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context.getApplicationContext(), "This user is requesting to be able to see the posts that you've made", Toast.LENGTH_SHORT).show();

                    }
                });

                b_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        relationship.setStatus(RelationshipStatus.VISIBLE);
                        FSH_INSTANCE.getInstance().fsh.editRelationship(relationship);
                        status.setText("Visible");
                        b_accept.setVisibility(View.GONE);
                        b_decline.setVisibility(View.GONE);
                    }
                });
                b_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        relationship.setStatus(RelationshipStatus.INVISIBLE);
                        FSH_INSTANCE.getInstance().fsh.editRelationship(relationship);
                        status.setText("Invisible");
                        b_accept.setVisibility(View.GONE);
                        b_decline.setVisibility(View.GONE);
                    }
                });
            } else if (rs == RelationshipStatus.PENDING_FOLLOWING) {

                // Depricated
                status.setText("Requesting to Follow");
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context.getApplicationContext(), "This user is requesting to be able to subscribe the posts that you've made", Toast.LENGTH_SHORT).show();
                    }
                });

                b_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FSH_INSTANCE.getInstance().fsh.setRelationship(sender, receiver, RelationshipStatus.FOLLOWING);
                        status.setText("Following");
                        b_accept.setVisibility(View.GONE);
                        b_decline.setVisibility(View.GONE);
                    }
                });
                b_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FSH_INSTANCE.getInstance().fsh.setRelationship(sender, receiver, RelationshipStatus.VISIBLE);
                        status.setText("Visible");
                        b_accept.setVisibility(View.GONE);
                        b_decline.setVisibility(View.GONE);
                    }
                });
            }
        }
        return view;
    }
}
