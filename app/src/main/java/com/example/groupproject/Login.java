package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
        String username, password;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_screen);
            initialize();
        }

        private void initialize() {
            Button signIn = findViewById(R.id.sign_in_button);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    username = getUsername();
                    password = getPassword();

                    validUsernameAndPassword(username, password);

                }
            });
        }

        public String getUsername() {
                EditText editUsername = findViewById(R.id.username_field);
                String userName = editUsername.getText().toString();

                return userName;
        }

        public String getPassword() {
            EditText editPassword = findViewById(R.id.password_field);
            String passWord = editPassword.getText().toString();
            return passWord;
        }

        public void validUsernameAndPassword(String username, String password) {
            boolean validUsername = true;
            boolean validPassword = true;

            if (username.isEmpty()) {
                validUsername = false;
            }
            // TODO: Conditional to check if the username is in the data base AND check usernames with their respective passwords
            if (password.isEmpty()) {
                validPassword = false;
            }
            if (!validUsername && !validPassword) {
                Toast toast = Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT);
                toast.show();
            } else if (!validUsername) {
                Toast toast = Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT);
                toast.show();
            } else if (!validPassword) {
                Toast toast = Toast.makeText(this, "No password entered", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

    public void createNewAccount(View view)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateAccountDialog createAccountDialog = new CreateAccountDialog();
        createAccountDialog.show(fragmentManager, "Create Account");


    }
}
