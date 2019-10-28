package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }


    public void clickAddMoodEvent(View view)
    {

    }
    public void clickMoodHistory(View view)
    {
        Intent intent = new Intent(this, com.example.groupproject.MoodEventListActivity.class);
        startActivity(intent);
    }

    public void clickFollowing(View view)
    {

    }

    public void clickMyFollowers(View view)
    {

    }
}
