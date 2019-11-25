package com.example.groupproject.ui.login;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;

import com.example.groupproject.R;
import com.example.groupproject.data.firestorehandler.FireStoreHandler;

public class SignOut extends DialogFragment {

    /**
     * Defines buttons and sets up listeners so that when the user clicks sign out and
     * confirms that they want to sign out then the app returns to the login screen.
     *
     * @author riona
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view, the view created using the layout inflater
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sign_out_dialog, container);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        getDialog().setCancelable(true);
        final FireStoreHandler fbAuth = new FireStoreHandler();

        // initialize buttons
        Button signOut = view.findViewById(R.id.sign_out_confirm);
        Button cancel = view.findViewById(R.id.sign_out_cancel);

        // set listener for if they confirm sign out
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbAuth.signOut(view);
            }
        });

        // set listener for if the user cancels sign out
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }
}