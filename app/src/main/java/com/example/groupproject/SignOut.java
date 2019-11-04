package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;

public class SignOut extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sign_out_dialog, container);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        getDialog().setCancelable(true);

        Button signOut = view.findViewById(R.id.sign_out_confirm);
        Button cancel = view.findViewById(R.id.sign_out_cancel);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeViews(view);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    public void changeViews(View v) {
        Intent intent = new Intent(getActivity(), com.example.groupproject.Login.class);
        startActivity(intent);
    }
}