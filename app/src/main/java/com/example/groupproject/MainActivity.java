package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.groupproject.data.firestorehandler.FSHConstructor;
import com.example.groupproject.data.user.User;
import com.example.groupproject.ui.login.SignOut;
import com.example.groupproject.ui.maps.MapsActivity;
import com.example.groupproject.ui.moodlists.AddMoodEventActivity;
import com.example.groupproject.ui.moodlists.MoodEventListActivity;
import com.example.groupproject.ui.relations.RelationshipViewActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Global Accessor
    public static FSHConstructor FSH_INSTANCE;
    public static User USER_INSTANCE;

    public MainActivity()
    {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        /* For testing from main screen */
        TextView curUserName = findViewById(R.id.tv_curUsername);
        curUserName.setText(USER_INSTANCE.getUserName());
    }


    public void clickAddMoodEvent(View view)
    {
        Intent intent = new Intent(this, AddMoodEventActivity.class);
        startActivity(intent);
    }
    public void clickMoodHistory(View view)
    {
        Intent intent = new Intent(this, MoodEventListActivity.class);
        startActivity(intent);
    }

    public void clickFollowing(View view)
    {
        Intent intent = new Intent(this, RelationshipViewActivity.class);
        startActivity(intent);
    }

    public void clickMyMap(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void clickSignOut(View view)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignOut signOutDialog = new SignOut();
        signOutDialog.show(fragmentManager, "Confirm sign out");
    }
}
