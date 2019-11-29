package com.example.groupproject.ui.maps;

import android.content.Context;
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
    String[] moodNames;
    Integer[] colors;

    /**
     * A constructor function to initiate the MapsSpinnerAdapter.
     * Names, Images and Colors array are passed for the spinner data.
     *
     * @author Andrew
     * @param context
     * @param textViewResourceId
     * @param names
     * @param images
     * @param colors
     */
    public MapsSpinnerAdapter(Context context, int textViewResourceId, String[] names, Integer[] images, Integer[] colors){
        super(context, textViewResourceId, names);
        this.images = images;
        this.moodNames = names;
        this.colors = colors;
    }

    /**
     * Function that's called to get the data of this adapter.
     * This function fetches the spinner with names / images / colors data
     *
     * @author Andrew
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
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

    /**
     * Function that's called to get the data from a dropdown view.
     * This function fetches the spinner with names / images / colors data
     *
     * @author Andrew
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getCustomView(position, convertView, parent);
    }

    /**
     * Function that's called to get the data from a view.
     * This function fetches the spinner with names / images / colors data
     *
     * @author Andrew
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    public View getView(int position, View convertView, ViewGroup parent){
        return getCustomView(position, convertView, parent);
    }
}
