package com.example.groupproject.ui.moodlists;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.groupproject.R;

import java.util.Calendar;


public class EditDateTimeDialog extends Dialog implements View.OnClickListener{

    Button dateButton;
    Button timeButton;


    public EditDateTimeDialog(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);


        initializeTextViews();

        dateButton.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Month from 0 - 11 so add 1
                        dateButton.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) );
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener(){
            Calendar calendar = Calendar.getInstance();

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes){
                        timeButton.setText(String.format("%02d:%02d", hour, minutes));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

    }

    private void initializeTextViews() {
        String year = Integer.toString(Calendar.getInstance().getTime().getYear() + 1900);
        int monthInt = Calendar.getInstance().getTime().getMonth();
        String month = (monthInt >= 10) ? Integer.toString(monthInt) : String.format("0%s",Integer.toString(monthInt));
        int dayInt = Calendar.getInstance().getTime().getDate();
        String day = (dayInt >= 10) ? Integer.toString(dayInt) : String.format("0%s",Integer.toString(dayInt));

        dateButton.setText(year + "-" + month + "-" + day);

        int hoursInt = Calendar.getInstance().getTime().getHours();
        String hours = (hoursInt >= 10) ? Integer.toString(hoursInt) : String.format("0%s",Integer.toString(hoursInt));

        int minutesInt = Calendar.getInstance().getTime().getMinutes();
        String minutes = (minutesInt >=10) ? Integer.toString(minutesInt) : String.format("0%s",Integer.toString(minutesInt));

        timeButton.setText(hours + ":" + minutes);
    }
}