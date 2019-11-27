package com.example.groupproject.ui.maps;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

//import androidx.annotation.Nullable;


public class MapsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    Integer[] images = {R.drawable.emot_happy_small, R.drawable.emot_sad_small, R.drawable.emot_angry_small, R.drawable.emot_anxious_small, R.drawable.emot_disgusted_small};
    ArrayList<String> moodNames = new ArrayList<>();
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
        System.out.println("New NEW NEW NEW NEW NRWEONROWNEIRWENROIEWN");
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
        System.out.println("12930130192830918290310923810209");


        //        setContentView()
        ImageView moodIconView = view.findViewById(R.id.moodIcon);
        TextView moodName = view.findViewById(R.id.moodName);
        TextView moodDateView = view.findViewById(R.id.moodDate);
        TextView moodReasonView = view.findViewById(R.id.moodReason);
        TextView moodImageTextView = view.findViewById(R.id.moodImageText);
        ImageView moodImageView = view.findViewById(R.id.moodImage);
        TextView moodSocialSituationView = view.findViewById(R.id.moodSocialSituation);

        Bundle bundle = getArguments();
        MoodEvent moodEvent = (MoodEvent)bundle.getSerializable("moodEvent");

        System.out.println(moodEvent.getMood().getName());

        moodIconView.setImageResource(iconMap.get(moodEvent.getMood().getName()));
        moodName.setText(moodEvent.getMood().getName());
        moodDateView.setText(moodEvent.getTimeStamp().getTime().toString());
        moodReasonView.setText((moodEvent.getReasonText() != null ? moodEvent.getReasonText() : ""));
        moodSocialSituationView.setText((moodEvent.getSocialSituation() != null ? moodEvent.getSocialSituation().toString() : ""));
//        System.out.println(moodEvent.getReasonImage() != null);
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
