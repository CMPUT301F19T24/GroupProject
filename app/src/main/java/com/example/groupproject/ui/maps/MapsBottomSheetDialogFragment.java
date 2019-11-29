package com.example.groupproject.ui.maps;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

//import androidx.annotation.Nullable;


public class MapsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    HashMap<String, Integer> iconMap;

    public static MapsBottomSheetDialogFragment newInstance(){
        return new MapsBottomSheetDialogFragment();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.activity_maps_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps_bottom_sheet_dialog, container,false);
        iconMap = new HashMap<>();
        iconMap.put("Happy", R.drawable.emot_happy_small);
        iconMap.put("Sad", R.drawable.emot_sad_small);
        iconMap.put("Angry", R.drawable.emot_angry_small);
        iconMap.put("Anxious", R.drawable.emot_anxious_small);
        iconMap.put("Disgusted", R.drawable.emot_disgusted_small);

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView moodIconView = view.findViewById(R.id.moodIcon);
        TextView moodName = view.findViewById(R.id.moodName);
        TextView moodDateView = view.findViewById(R.id.moodDate);
        TextView moodReasonView = view.findViewById(R.id.moodReason);
        TextView moodImageTextView = view.findViewById(R.id.moodImageText);
        ImageView moodImageView = view.findViewById(R.id.moodImage);
        TextView moodSocialSituationView = view.findViewById(R.id.moodSocialSituation);
        LinearLayout moodBarView = view.findViewById(R.id.moodBar);

        Bundle bundle = getArguments();
        MoodEvent moodEvent = (MoodEvent)bundle.getSerializable("moodEvent");

        System.out.println(moodEvent.getMood().getName());

        moodIconView.setImageResource(iconMap.get(moodEvent.getMood().getName()));
        moodName.setText(moodEvent.getMood().getName());
        moodDateView.setText(moodEvent.getTimeStamp().getTime().toString());
        moodReasonView.setText((moodEvent.getReasonText() != null ? moodEvent.getReasonText() : ""));
        moodSocialSituationView.setText((moodEvent.getSocialSituation() != null ? moodEvent.getSocialSituation().toString() : ""));

        moodBarView.setBackgroundColor(moodEvent.getMood().getColor());
        if(moodEvent.getReasonImage() == null){
            moodImageTextView.setVisibility(View.GONE);
            moodImageView.setVisibility(View.GONE);
        }
        else{
            moodImageTextView.setVisibility(View.VISIBLE);
            moodImageView.setVisibility(View.VISIBLE);
            moodImageView.setImageBitmap(moodEvent.getReasonImage());
        }


    }
}
