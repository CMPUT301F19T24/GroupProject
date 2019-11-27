package com.example.groupproject.ui.relations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.user.User;

import java.util.ArrayList;

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class RelationshipResponsesFragment extends Fragment {
    private ListView userListView;
    private ArrayList<User> userFollowerList;
    ArrayList<Relationship> localRelationships;
    ArrayAdapter<Relationship> relationshipContainer;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localRelationships = FSH_INSTANCE.getInstance().fsh.getPendingResponsesOfUser(USER_INSTANCE.getUserName());
        relationshipContainer = new ResponsesList(this.getContext(), localRelationships);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        localRelationships = FSH_INSTANCE.getInstance().fsh.getPendingResponsesOfUser(USER_INSTANCE.getUserName());
        relationshipContainer.notifyDataSetChanged();

        View root = inflater.inflate(R.layout.fragment_responses, viewGroup, false);
        userListView = root.findViewById(R.id.lv_responses_list);
        userListView.setAdapter(relationshipContainer);

        userListView.setEmptyView(root.findViewById(R.id.tv_empty_responses));

        return root;
    }
}
