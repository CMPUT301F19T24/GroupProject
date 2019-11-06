package com.example.groupproject;

import java.util.ArrayList;

import static com.example.groupproject.SortingMethod.*;

public class ChooseMoodEvent implements Comparable {
    private Mood mood;

    ChooseMoodEvent(Mood currentMood)
    {
        this.mood = currentMood;
    }

    public Mood getMood()
    {
        return this.mood;
    }

    public void setMood(Mood mood)
    {
        this.mood = mood;
    }

    @Override
    public int compareTo(Object o) { // By default, sort by date
        return compareTo(o, DATE);
    }

    public int compareTo(Object o, SortingMethod sm)
    {
        switch(sm)
        {
            case NAME:
                return this.mood.compareTo(((MoodEvent) o).getMood());

            default:
                throw new IllegalStateException("Unexpected value: " + sm);
        }
    }

    public boolean contains(String query)
    {
        String[] parsedQuery = query.split(" ");

        Boolean rc = false;
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(mood.getName());

        for(String i : parsedQuery)
        {
            for(String j : checkList)
            {
                if(j.toLowerCase().contains(i.toLowerCase()))
                {
                    rc = true;
                    break;
                }
            }
        }

        return rc;
    }
}
