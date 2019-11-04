package com.example.groupproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CreateAccountDialog extends DialogFragment {

    String newUsername, newPassword;
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
        Button confirm = view.findViewById(R.id.register_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getUsername();
                String password = getPassword();
                String verifyPassword = getConfirmPassword();

                validUsername = checkUsername(username);
                validPassword = checkPassword(password);
                passwordsMatch = checkPasswordMatch(password, verifyPassword);


                if (!validPassword && !validUsername) {
                    Toast toast = Toast.makeText(getContext(), "Invalid Username and Password", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (!validPassword) {
                    Toast toast = Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (!validUsername) {
                    Toast toast = Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (!passwordsMatch) {
                    Toast toast = Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                        // Save the username and password as a key-value pair to
                        getDialog().dismiss();
                }
            }
        });
    }

    String getUsername() {
        View view = getView();
        EditText editUsername = view.findViewById(R.id.new_username);
        String username = editUsername.getText().toString();
        return username;
    }

    String getPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.new_password);
        String password = editPassword.getText().toString();
        return password;
    }

    String getConfirmPassword() {
        View view = getView();
        EditText editPassword = view.findViewById(R.id.confirm_password);
        String password = editPassword.getText().toString();
        return password;
    }

    // Still needs to check if the username is available.
    boolean checkUsername(String username) {
        boolean valid = true;
        if (username.isEmpty()){
            valid = false;
        }
        return valid;
    }

    boolean checkPassword(String password) {
        boolean valid = true;
        if (password.isEmpty()){
            valid = false;
        }
        return valid;
    }

    boolean checkPasswordMatch(String password, String verifyPassword) {
        boolean valid = true;
        if (password != verifyPassword) {
            valid = false;
        }
        return valid;
    }
}
