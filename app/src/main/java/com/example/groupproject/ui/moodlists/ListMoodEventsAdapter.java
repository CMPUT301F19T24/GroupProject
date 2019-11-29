package com.example.groupproject.ui.moodlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.R;
import com.example.groupproject.data.user.User;
import com.example.groupproject.data.moods.Mood;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class ListMoodEventsAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<MoodEvent> moodEventArrayList;
    SortingMethod sm;

    public ListMoodEventsAdapter(@NonNull Context context, ArrayList<MoodEvent> moodEventArrayList, SortingMethod defaultSM) {
        super(context, 0, moodEventArrayList);
        this.context = context;
        this.moodEventArrayList = moodEventArrayList;
        this.sm = defaultSM;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /**
         * Construct the elements
         *
         * @param - See base method for details
         * @return - See base method for details
         */
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.context).inflate(R.layout.e_list_mood_events,parent,false);

        // Initialize the fields
        MoodEvent curMoodEvent = this.moodEventArrayList.get(position);

        Mood curMood = curMoodEvent.getMood();
        Calendar curTimeStamp = curMoodEvent.getTimeStamp();
        User curOwner = curMoodEvent.getOwner();

        LinearLayout linearLayout = listItem.findViewById(R.id.ll_list_mood_events);

        TextView tvMoodName = listItem.findViewById(R.id.e_tv_mood_name);
        TextView tvTimeStamp = listItem.findViewById(R.id.e_tv_timestamp);
        TextView tvUsername = listItem.findViewById(R.id.e_tv_username);

        ImageView emoticon = listItem.findViewById(R.id.e_img_emoticon);

        String moodNameStr = curMood.getName();
        String curTimeStampStr = String.format("%d-%d-%d", curTimeStamp.get(Calendar.DATE), curTimeStamp.get(Calendar.MONTH)+1, curTimeStamp.get(Calendar.YEAR));
        String usernameStr = curOwner.getUserName();

        // Populate the fields
        try
        {
            tvMoodName.setText(moodNameStr);
            tvTimeStamp.setText(curTimeStampStr);
            tvUsername.setText(usernameStr);

            linearLayout.setBackgroundColor(curMood.getColor());
            emoticon.setImageResource(curMood.getImage());

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.notifyDataSetChanged();

        return listItem;
    }

    public void setSortingMethod(final SortingMethod sm)
    {
        /**
         * Set the sorting method to order the moodevents by
         * See SortingMethod for details
         *
         * @param sm - Sorting method to use
         */
        this.sm = sm;

        this.sort(new Comparator<MoodEvent>() {
            @Override
            public int compare(MoodEvent a, MoodEvent b) {
                return a.compareTo(b, sm);
            }
        });

        this.notifyDataSetChanged();
    }
}
