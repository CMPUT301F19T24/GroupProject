package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    String username, password;

    /**
     * Changes the view to login_screen.xml
     *
     * @author riona
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        initialize();
        }

    /**
     * Sets up listener to tell when the user hits the sign in button
     * Then checks whether the username and password are valid and in the database.
     *
     * @author riona
     */
    private void initialize() {
        Button signIn = findViewById(R.id.sign_in_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = getUsername();
                password = getPassword();
                validUsernameAndPassword(username, password, view);
            }
        });
    }

    /**
     * Gets the username that the user entered
     *
     * @author riona
     * @return username as a String
     */
    public String getUsername() {
                EditText editUsername = findViewById(R.id.username_field);
                String userName = editUsername.getText().toString();

                return userName;
        }

    /**
     * Gets the password the user entered
     *
     * @author riona
     * @return password as a String
     */
    public String getPassword() {
            EditText editPassword = findViewById(R.id.password_field);
            String passWord = editPassword.getText().toString();
            return passWord;
        }

    /**
     * Checks whether the username and password entered are valid
     * Conditions for valid username:
     *      - Not empty string
     *      - Username is in database
     * Conditions for valid password:
     *      - password is at least 6 characters
     *
     * @author riona
     * @param username
     * @param password
     */
    public void validUsernameAndPassword(String username, String password, View view) {
        FireStoreHandler fbAuth = new FireStoreHandler();
        boolean validUsername = true;
        boolean validPassword = true;
        EditText editUsername = findViewById(R.id.username_field);
        EditText editPassword = findViewById(R.id.password_field);

        if (username.isEmpty()) {
            validUsername = false;
            editUsername.setError("Invalid username");
        }
        if (password.length() < 6) {
            validPassword = false;
            editPassword.setError("Invalid password");
        }

        if (validPassword && validUsername) {
            fbAuth.login(username, password, view);
        }
    }

    /**
     * Opens dialog fragment for creating a new account
     *
     * @author riona
     * @param view
     */
    public void createNewAccount(View view)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateAccountDialog createAccountDialog = new CreateAccountDialog();
        createAccountDialog.show(fragmentManager, "Create Account");

    }
}
