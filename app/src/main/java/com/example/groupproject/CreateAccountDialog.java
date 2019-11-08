package com.example.groupproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static android.content.ContentValues.TAG;
/*
Some general information:
        1. Firebase min password length is 6 characters
        2. Format for username: [username]@cmput301-c6741.web.app ----> "@cmput301-c6741.web.app" needs to be appended
        3.
*/

public class CreateAccountDialog extends DialogFragment {

    boolean validUsername, validPassword, passwordsMatch;

    /**
     * Function that is called when the CreateAccount dialog is created.
     * Creates the view on to be displayed on the phone/emulator.
     *
     * @author riona
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("CreateAccountDialog");
        final View view = inflater.inflate(R.layout.create_account_dialog, container);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        return view;
    }

    /**
     * This function automatically runs once the view is created
     * Sets up functionality for the "REGISTER" button on the account creation dialog.
     *
     * @author riona
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_LONG);
        Button confirm = view.findViewById(R.id.register_confirm);
        final FireStoreHandler fbAuth = new FireStoreHandler();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getUsername();
                String password = getPassword();
                String verifyPassword = getConfirmPassword();

                validUsername = checkUsername(username);
                validPassword = checkPassword(password);
                passwordsMatch = checkPasswordMatch(password, verifyPassword);

                if (validUsername && validPassword && passwordsMatch) {
                    fbAuth.createNewUser(username, password, view, getDialog());

                }
            }
        });
    }

    /**
     * Gets the username that the user entered.
     *
     * @author riona
     * @return username as a string
     */
    private String getUsername() {
        View view = getView();
        EditText editUsername = view.findViewById(R.id.new_username);
        String username = editUsername.getText().toString();
        return username;
    }

    /**
     * Gets the password that the user entered.
     *
     * @author riona
     * @return password as a string
     */
    private String getPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.new_password);
        String password = editPassword.getText().toString();
        return password;
    }

    /**
     * Gets the confirmed password
     *
     * @author riona
     * @return confirmed password as a string
     */
    private String getConfirmPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.confirm_password);
        String password = editPassword.getText().toString();
        return password;
    }

    /**
     * Checks whether the username that was entered is valid
     * Conditions for a valid username:
     *      - String cannot be empty
     *
     * @author riona
     * @param username
     * @return valid, when valid == true the username is valid,
     *                when valid == false: the username is not valid
     */
    private boolean checkUsername(String username) {
        boolean valid = true;
        EditText editUsername = getView().findViewById(R.id.new_username);

        if (username.isEmpty()){
            editUsername.setError("This field cannot be empty");
            valid = false;
        }
        return valid;
    }

    /**
     * Checks whether the password is valid
     * Conditions for a valid password:
     *      - Cannot be empty string
     *      - Must be at least 6 characters long (checked by FireStoreHandler)
     *
     * @author riona
     * @param password
     * @return valid, when valid == true the password is valid,
     *                when valid == false: the password is not valid
     */
    private boolean checkPassword(String password) {
        boolean valid = true;
        EditText editPassword = getView().findViewById(R.id.new_password);
        if (password.isEmpty()){
            editPassword.setError("This field cannot be empty");
            valid = false;
        }
        return valid;
    }

    /**
     * Checks whether the entered password matches the confirm password
     *
     * @author riona
     * @param password
     * @param verifyPassword
     * @return valid, when valid == true the passwords match,
     *                when valid == false: the username is not valid
     */
    private boolean checkPasswordMatch(String password, String verifyPassword) {
        boolean valid = true;
        EditText editPassword = getView().findViewById(R.id.confirm_password);
        if (!password.equals(verifyPassword)) {
            editPassword.setError("Passwords must match");
            valid = false;
        }
        return valid;
    }
}
