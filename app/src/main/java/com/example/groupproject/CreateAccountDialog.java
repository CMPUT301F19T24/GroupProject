package com.example.groupproject;

import android.os.Bundle;
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
/*
Some general information:
        1. Firebase min password length is 6 characters
        2. Format for username: [username]@cmput301-c6741.web.app ----> "@cmput301-c6741.web.app" needs to be appended
        3.
*/
public class CreateAccountDialog extends DialogFragment {

    boolean validUsername, validPassword, passwordsMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.create_account_dialog, container);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_LONG);
        Button confirm = view.findViewById(R.id.register_confirm);
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
                    // Save the username and password as a key-value pair to database
                    getDialog().dismiss();
                }
            }
        });
    }

    private String getUsername() {
        View view = getView();
        EditText editUsername = view.findViewById(R.id.new_username);
        String username = editUsername.getText().toString();
        return username;
    }

    private String getPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.new_password);
        String password = editPassword.getText().toString();
        return password;
    }

    private String getConfirmPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.confirm_password);
        String password = editPassword.getText().toString();
        return password;
    }

    // Still needs to check if the username is available.
    private boolean checkUsername(String username) {
        boolean valid = true;
        if (username.isEmpty()){
            Toast toast = Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_SHORT);
            toast.show();
            valid = false;
        }
        return valid;
    }

    private boolean checkPassword(String password) {
        boolean valid = true;
        if (password.isEmpty()){
            Toast toast = Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_SHORT);
            toast.show();
            valid = false;
        }
        return valid;
    }

    private boolean checkPasswordMatch(String password, String verifyPassword) {
        boolean valid = true;
        if (!password.equals(verifyPassword)) {
            Toast toast = Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT);
            toast.show();
            valid = false;
        }
        return valid;
    }
}
