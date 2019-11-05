package com.example.groupproject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class ChooseMoodAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<ChooseMoodEvent> moodEventArrayList;

    public ChooseMoodAdapter(@NonNull Context context, ArrayList<ChooseMoodEvent> moodEventArrayList) {
        super(context, 0, moodEventArrayList);
        this.context = context;
        this.moodEventArrayList = moodEventArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.context).inflate(R.layout.e_list_mood_events,parent,false);

        ChooseMoodEvent curMoodEvent = this.moodEventArrayList.get(position);

        Mood curMood = curMoodEvent.getMood();

        LinearLayout linearLayout = listItem.findViewById(R.id.ll_list_mood_events);

        TextView tvMoodName = listItem.findViewById(R.id.e_tv_mood_name);
        ImageView emoticon = listItem.findViewById(R.id.e_img_emoticon);

        String moodNameStr = curMood.getName();

        try
        {
            tvMoodName.setText(moodNameStr);

            linearLayout.setBackgroundColor(curMood.getColor());
            emoticon.setImageResource(curMood.getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.notifyDataSetChanged();

        return listItem;
    }
}
