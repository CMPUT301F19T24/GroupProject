package com.example.groupproject;

import android.location.Location;
import android.media.Image;

import java.util.ArrayList;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Calendar;

import static com.example.groupproject.SortingMethod.*;

public class MoodEvent implements Comparable {
    private Mood mood;
    private Calendar timeStamp;
    private SocialSituation socialSituation;
    private String reasonText;
    private Image reasonImage;
    private Location location;
    private User owner;

    private FusedLocationProviderClient fusedLocationClient;

              MoodEvent(Mood currentMood,
              Calendar timeStamp,
              User owner,
              SocialSituation socialSituation,
              String reasonText,
              Image reasonImage,
              Location location)
    {
        this.mood = currentMood;
        this.timeStamp = timeStamp;
        this.owner = owner;
        this.socialSituation = socialSituation; // Optional
        this.reasonText = reasonText; // Optional
        this.reasonImage = reasonImage; // Optional
        this.location = location; // Optional
    }

    public Mood getMood()
    {
        return this.mood;
    }

    public Calendar getTimeStamp()
    {
        return this.timeStamp;
    }

    public SocialSituation getSocialSituation()
    {
        return this.socialSituation;
    }

    public String getReasonText()
    {
        return this.reasonText;
    }

    public Image getReasonImage()
    {
        return this.reasonImage;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public User getOwner()
    {
        return this.owner;
    }

    public void setMood(Mood mood)
    {
        this.mood = mood;
    }

    public void setTimeStamp(Calendar timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void setSocialSituation(SocialSituation socialSituation)
    {
        this.socialSituation = socialSituation;
    }

    public void setReasonText(String reasonText)
    {
        this.reasonText = reasonText;
    }

    public void setReasonImage(Image reasonImage)
    {
        this.reasonImage = reasonImage;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }


    @Override
    public int compareTo(Object o) { // By default, sort by date
        return compareTo(o, DATE);
    }

    public int compareTo(Object o, SortingMethod sm)
    {
        /**
         * Returns the comparasion depending on the sorting method
         *
         * @param sm - See SortingMethod for details
         */
        switch(sm)
        {
            case NAME:
                return this.mood.compareTo(((MoodEvent) o).getMood());
            case DATE:
                return this.timeStamp.compareTo(((MoodEvent) o).getTimeStamp());
            case OWNER:
                return this.owner.getUserName().compareTo(((MoodEvent) o).getOwner().getUserName());
            default:
                throw new IllegalStateException("Unexpected value: " + sm);
        }
    }

    public boolean contains(String query)
    {
        /**
         * Determines whether or not the moodevent contains the query in anyway, shape or form.
         *
         * Checks the following:
         * - MoodName
         * - Timestamp
         * - Owner name
         *
         * @param query - String to search by
         */
        String[] parsedQuery = query.split(" ");

        Boolean rc = false;
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(mood.getName());
        checkList.add(String.valueOf(timeStamp.get(Calendar.YEAR)));
        checkList.add(String.valueOf(timeStamp.get(Calendar.MONTH)));
        checkList.add(String.valueOf(timeStamp.get(Calendar.DATE)));
        checkList.add(owner.getUserName());

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
