package com.example.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import static android.content.ContentValues.TAG;



public class FBAuth {
    public FirebaseAuth fbAuth = FirebaseAuth.getInstance();


    public void login(String username, final String password, final View view) {
        username = username + "@cmput301-c6741.web.app";
        Exception exception = null;
        fbAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        EditText passwordText = view.getRootView().findViewById(R.id.password_field);
                        EditText editText = view.getRootView().findViewById(R.id.username_field);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loginUserWithEmail:successful");
                            Intent intent = new Intent(view.getRootView().getContext(), MainActivity.class);
                            view.getRootView().getContext().startActivity(intent);
                        } else {
                            Log.w(TAG, "loginUserWithEmail:failed");
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                editText.setError("User does not exist");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passwordText.setError("Incorrect Password");
                            } catch (Exception e) {
                                Toast.makeText(view.getContext(), "An error occured while logging in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

//    public void signOut() {
//        fbAuth.signOut();
//    }
}
