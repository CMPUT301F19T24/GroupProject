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
        /* For testing from main screen */
//        setContentView(R.layout.main_screen);

        /* For Testing the Login Screen and Account Creation Dialog */
//        setContentView(R.layout.login_screen);
//        TextView createAccount = findViewById(R.id.create_Account);
//        createAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createNewAccount();
//            }
//        });
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
  
      private void createNewAccount() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateAccountDialog createAccountDialog = new CreateAccountDialog();
        createAccountDialog.show(fragmentManager, "Dialog Pop-up");
    }
}
