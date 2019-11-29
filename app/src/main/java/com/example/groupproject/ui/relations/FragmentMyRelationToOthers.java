package com.example.groupproject.ui.relations;

import android.os.Bundle;
import android.util.Log;
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

import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;

public class FragmentMyRelationToOthers extends Fragment {
    private ListView userListView;
    ArrayAdapter<Relationship> relationshipContainer;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        relationshipContainer = new ListViewMyRelationToOthers(this.getContext(), getRelationships());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.f_my_relation_to_others, viewGroup, false);
        userListView = root.findViewById(R.id.lv_my_relation_to_others);
        userListView.setAdapter(relationshipContainer);

        userListView.setEmptyView(root.findViewById(R.id.tv_empty_my_relation_to_others));
        relationshipContainer.notifyDataSetChanged();
        return root;
    }

    private ArrayList<Relationship> getRelationships()
    {
        ArrayList<Relationship> rc = new ArrayList<>();
        ArrayList<String> foundUsername = new ArrayList<>();


        for(Relationship i : FSH_INSTANCE.getInstance().fsh.getAllCachedRelationships())
        {
            if(i.getSender().getUserName().compareTo(USER_INSTANCE.getUserName()) == 0)
            {
                System.out.println("ABC" + i.toString());
                foundUsername.add(i.getRecipiant().getUserName());
                rc.add(i);
            }
        }

        foundUsername.remove(USER_INSTANCE.getUserName());

        for(User i : FSH_INSTANCE.getInstance().fsh.getAllUsers() )
        {
            System.out.println(!foundUsername.contains(i.getUserName()));

            if(!foundUsername.contains(i.getUserName()) && i.getUserName().compareTo(USER_INSTANCE.getUserName()) != 0)
            {
                rc.add(new Relationship(USER_INSTANCE, i, RelationshipStatus.INVISIBLE));

            }
        }

        return rc;
    }
}
