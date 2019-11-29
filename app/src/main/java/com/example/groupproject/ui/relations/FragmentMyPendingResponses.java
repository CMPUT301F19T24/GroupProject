package com.example.groupproject.ui.relations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;
import com.example.groupproject.data.user.User;

import java.util.ArrayList;

import static android.content.Context.USER_SERVICE;
import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class FragmentMyPendingResponses extends Fragment {
    private ListView userListView;
    ArrayAdapter<Relationship> relationshipContainer;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relationshipContainer = new ListViewMyPendingResponses(this.getContext(), getRelationships());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.f_my_pending_responses, viewGroup, false);
        userListView = root.findViewById(R.id.lv_my_pending_responses);
        userListView.setAdapter(relationshipContainer);

        userListView.setEmptyView(root.findViewById(R.id.tv_empty_my_pending_responses));

        relationshipContainer.notifyDataSetChanged();
        return root;
    }

    private ArrayList<Relationship> getRelationships()
    {
        ArrayList<Relationship> rc = new ArrayList<>();

        for(Relationship i : FSH_INSTANCE.getInstance().fsh.getAllCachedRelationships())
        {
            if(i.getRecipiant().getUserName().compareTo(USER_INSTANCE.getUserName()) == 0 &&
               i.getStatus() == RelationshipStatus.PENDING)
            {
                rc.add(i);
            }
        }

        return rc;
    }

}
