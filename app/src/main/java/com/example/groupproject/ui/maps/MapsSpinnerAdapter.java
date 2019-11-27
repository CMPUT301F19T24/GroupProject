package com.example.groupproject.ui.maps;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groupproject.R;

public class MapsSpinnerAdapter extends ArrayAdapter {

    Integer[] images = {0, R.drawable.emot_happy_small, R.drawable.emot_sad_small, R.drawable.emot_angry_small, R.drawable.emot_anxious_small, R.drawable.emot_disgusted_small};
    String[] moodNames = {"Show All", "Happy", "Sad", "Angry", "Anxious", "Disgusted"};

    public MapsSpinnerAdapter(Context context, int textViewResourceId, String[] names){
        super(context, textViewResourceId, names);
    }

    public View getCustomView (int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_maps_spinner, parent, false);

        ImageView imageView = layout.findViewById(R.id.mapSpinnerImage);
        imageView.setImageResource(images[position]);

        TextView textView = layout.findViewById(R.id.mapSpinnerText);
        textView.setText(moodNames[position]);

        return layout;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getCustomView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        return getCustomView(position, convertView, parent);
    }
}
