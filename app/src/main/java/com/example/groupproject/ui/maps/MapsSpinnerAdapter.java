package com.example.groupproject.ui.maps;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.groupproject.R;

public class MapsSpinnerAdapter extends ArrayAdapter {

    Integer[] images;
//            = {0, R.drawable.emot_happy_small, R.drawable.emot_sad_small, R.drawable.emot_angry_small, R.drawable.emot_anxious_small, R.drawable.emot_disgusted_small};
    String[] moodNames;
//            = {"Show All", "Happy", "Sad", "Angry", "Anxious", "Disgusted"};
    Integer[] colors;
//        = {0xfff0f0f0, 0x5bffff00, 0x5b0090ff, 0x5bff0000, 0x5bC997ff, 0x5b00ff00};

    public MapsSpinnerAdapter(Context context, int textViewResourceId, String[] names, Integer[] images, Integer[] colors){
        super(context, textViewResourceId, names);
        this.images = images;
        this.moodNames = names;
        this.colors = colors;
    }

    public View getCustomView (int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_maps_spinner, parent, false);

        LinearLayout spinnerElement = layout.findViewById(R.id.spinnerElement);
        spinnerElement.setBackgroundColor(colors[position]);

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
