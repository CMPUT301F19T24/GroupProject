package com.example.groupproject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class FireStoreHandler {
    private ArrayList<MoodEvent> cachedMoodEvents;
    private ArrayList<User> cachedUsers;
    private ArrayList<Relationship> cachedRelationship;
    public FireStoreHandler()
    {

    }

    // Communicates with Remote
    private void requestMoodEventListFromRemote()
    {
        // TBD
    }

    private void requestUserListFromRemote()
    {
        // TBD
    }

    private void requestRelationshipsFromRemotes()
    {
        // TBD
    }

    private void updateAllCachedLists()
    {
        requestMoodEventListFromRemote();
        requestUserListFromRemote();
        requestRelationshipsFromRemotes();
    }

    public void injectTestData(ArrayList<MoodEvent> arr_me, ArrayList<User> arr_u, ArrayList<Relationship> arr_rs)
    {
        this.cachedMoodEvents = arr_me;
        this.cachedUsers = arr_u;
        this.cachedRelationship = arr_rs;
    }

    public ArrayList<MoodEvent> getMoodEventsByUsername(String username)
    {
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getOwner().getUserName() == username)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getMoodEventsByMoodName(String moodName)
    {
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getMood().getName() == moodName)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getMoodEventsByMoodNameAndUserName(String moodName, String username)
    {
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getMood().getName() == moodName && i.getOwner().getUserName() == username)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getVisibleMoodEvents(String username)
    {
        updateAllCachedLists();
        ArrayList<MoodEvent> arr_me = new ArrayList<>();
        ArrayList<Relationship> arr_rs = getRelationShipOfSender(username);

        ArrayList<String> visibleUsers = new ArrayList<>();

        for (Relationship i : arr_rs)
        {
            if(i.isVisible())
            {
                visibleUsers.add(i.getRecipiant().getUserName());
            }
        }

        for(MoodEvent j : cachedMoodEvents)
        {
            if(visibleUsers.contains(j.getOwner().getUserName() ))
            {
                arr_me.add(j);
            }
        }
        return arr_me;
    }

    public ArrayList<Relationship> getRelationShipOfSender(String username)
    {
        ArrayList<Relationship> arr_rs = new ArrayList<>();
        for(Relationship i : cachedRelationship)
        {
            if(i.getSender().getUserName() == username)
            {
                arr_rs.add(i);
            }
        }
        return arr_rs;
    }
}
