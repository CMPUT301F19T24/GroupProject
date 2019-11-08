package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Global Accessor
    public static FSHConstructor FSH_INSTANCE; public static ArrayList<User> TEMP_CACHED_USERS_LIST;
    public static User USER_INSTANCE;

    public MainActivity()
    {
        super();
        FSH_INSTANCE.getInstance();
        USER_INSTANCE = FSH_INSTANCE.getInstance().fsh.getUserObjWIthUsername("Darth Vader");
        TEMP_CACHED_USERS_LIST = FSH_INSTANCE.getInstance().fsh.getCachedUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* For testing from main screen */

        setContentView(R.layout.main_screen);
    }

    public void clickAddMoodEvent(View view)
    {
        Intent intent = new Intent(this, com.example.groupproject.AddMoodEventActivity.class);
        startActivity(intent);
    }
    public void clickMoodHistory(View view)
    {
        Intent intent = new Intent(this, com.example.groupproject.MoodEventListActivity.class);
        startActivity(intent);
    }

    public void clickFollowing(View view)
    {
        Intent intent = new Intent(this, com.example.groupproject.relations.class);
        startActivity(intent);
    }

    public void clickMyMap(View view)
    {
        Intent intent = new Intent(this, com.example.groupproject.MapsActivity.class);
        startActivity(intent);
    }

    public void clickSignOut(View view)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignOut signOutDialog = new SignOut();
        signOutDialog.show(fragmentManager, "Confirm sign out");
    }
}
