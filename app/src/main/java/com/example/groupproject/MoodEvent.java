package com.example.groupproject;

import android.location.Location;
import android.media.Image;

import java.util.Arrays;
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

              MoodEvent(Mood currentMood,
              Calendar timeStamp,
              SocialSituation socialSituation,
              String reasonText,
              Image reasonImage,
              Location location,
              Integer moodId)
    {
        this.mood = currentMood;
        this.timeStamp = timeStamp;
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
        switch(sm)
        {
            case NAME:
                return this.mood.compareTo(((MoodEvent) o).getMood());
            case DATE:
                return this.timeStamp.compareTo(((MoodEvent) o).getTimeStamp());
            default:
                throw new IllegalStateException("Unexpected value: " + sm);
        }
    }
}
