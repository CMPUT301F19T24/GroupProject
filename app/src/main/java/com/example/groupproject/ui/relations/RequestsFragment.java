package com.example.groupproject.ui.relations;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.groupproject.R;
import com.example.groupproject.User;

import java.util.ArrayList;

import static com.example.groupproject.MainActivity.TEMP_CACHED_USERS_LIST;

public class RequestsFragment extends Fragment {
    /**
     * @author: vivek
     * Handles displaying of requests fragment under Requests Tab
     */
    private ListView userListView;
    private ArrayAdapter<User> userArrayAdapter;
    private ArrayList<User> userRequestList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRequestList = new ArrayList<>();
        for (int i = 0; i < TEMP_CACHED_USERS_LIST.size(); i++){
            userRequestList.add(i, TEMP_CACHED_USERS_LIST.get(i));
        }
        userArrayAdapter = new CustomRequestList(this.getContext(), userRequestList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        /**
         * List view population with requests from users when creating.
         */
        View root = inflater.inflate(R.layout.fragment_relations, viewGroup, false);
        userListView = root.findViewById(R.id.users_list);
        TextView messageView = root.findViewById(R.id.section_label);
        messageView.setText("Once implemented, will display incoming requests");
        userListView.setAdapter(userArrayAdapter);
        return root;
    }
}
