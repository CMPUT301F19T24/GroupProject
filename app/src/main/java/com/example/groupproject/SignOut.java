package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;

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

        // initialize buttons
        Button signOut = view.findViewById(R.id.sign_out_confirm);
        Button cancel = view.findViewById(R.id.sign_out_cancel);

        // set listener for if they confirm sign out
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeViews(view);
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

    /**
     * Swaps the view from current screen to login screen.
     *
     * @author riona
     * @param v
     */
    public void changeViews(View v) {
        Intent intent = new Intent(getActivity(), com.example.groupproject.Login.class);
        startActivity(intent);
    }
}