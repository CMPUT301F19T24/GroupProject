package com.example.groupproject.ui.relations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.groupproject.R;
import com.example.groupproject.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.groupproject.MainActivity.USER_INSTANCE;
import static com.example.groupproject.MainActivity.TEMP_CACHED_USERS_LIST;
import static com.example.groupproject.MainActivity.FSH_INSTANCE;

/**
 * A placeholder fragment containing a simple view to demonstrate follower/following relationships.
 */
public class GeneralizedFragment extends Fragment {

    private ListView userListView;
    private ArrayAdapter<User> userAdapter;
    private ArrayList<User> userDataList;
    private ArrayList<User> followerDataList;
    private ArrayList<User> followingDataList;
    private ArrayList<User> requestsDataList;
    private ArrayAdapter<User> userRequestAdapter;
    private ArrayAdapter<User> followingAdapter;
    private ArrayAdapter<User> followerAdapter;
    private ArrayAdapter<User> requestsAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static GeneralizedFragment newInstance(int index) {
        GeneralizedFragment fragment = new GeneralizedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        // Add user into userDataList
        userDataList = new ArrayList<>();
//        FSH_INSTANCE.getInstance().fsh.getUserObjWithUserName();
        for (int i = 0; i < TEMP_CACHED_USERS_LIST.size(); i++){
            userDataList.add(i, TEMP_CACHED_USERS_LIST.get(i));
        }

//        userDataList.add(USER_INSTANCE);
        userAdapter = new CustomUserList(this.getContext(), userDataList);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_relations, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        userListView = root.findViewById(R.id.users_list);

        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                userListView.setAdapter(userAdapter);
            }
        });
        return root;
    }
}