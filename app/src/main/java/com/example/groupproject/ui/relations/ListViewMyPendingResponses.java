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

import static com.example.groupproject.MainActivity.FSH_INSTANCE;

public class ListViewMyPendingResponses extends ArrayAdapter<Relationship> {
    /**
     * Implements a custom Array adapter for viewing users in a list.
     * @author Vivek
     *
     */
    private List<Relationship> relations;
    private Context context;


    public ListViewMyPendingResponses(Context context, List<Relationship> relations){
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
         * Return the fragment view of pending requests
         * @author - Donald
         * @param - See parent class
         * @return - See parent class
         */
        View view = convertView;

        final Relationship rs = this.relations.get(position);
        final String username = rs.getSender().getUserName();
        final RelationshipStatus status = rs.getStatus();

        view = LayoutInflater.from(context).inflate(R.layout.e_my_pending_responses, parent, false);
        final TextView tv_username = view.findViewById(R.id.e_tv_my_pending_responses_username);

        tv_username.setText(username);

        final Button b_accept = view.findViewById(R.id.b_responses_accept);
        final Button b_decline = view.findViewById(R.id.b_responses_decline);

        b_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * On click, set the status to following
                 *
                 * @author - Donald
                 */
                rs.setStatus(RelationshipStatus.FOLLOWING);

                FSH_INSTANCE.getInstance().fsh.updateRelationshipStatusOnRemote(rs);

                b_accept.setVisibility(View.GONE);
                b_decline.setVisibility(View.GONE);
            }
        });

        b_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * On click, set the status to invisible
                 *
                 * @author - Donald
                 */
                rs.setStatus(RelationshipStatus.INVISIBLE);

                FSH_INSTANCE.getInstance().fsh.updateRelationshipStatusOnRemote(rs);
                b_accept.setVisibility(View.GONE);
                b_decline.setVisibility(View.GONE);
            }
        });
        return view;
    }
}
