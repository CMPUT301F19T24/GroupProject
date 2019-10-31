package com.example.groupproject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class FireStoreHandler {
    protected ArrayList<MoodEvent> cachedMoodEvents;
    protected ArrayList<User> cachedUsers;
    protected ArrayList<Relationship> cachedRelationship;
    public FireStoreHandler()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        updateAllCachedLists();
    }

    // Communicates with Remote
    protected void pullMoodEventListFromRemote()
    {
        // TBD
    }

    protected void pullUserListFromRemote()
    {
        // TBD
    }

    protected void pullRelationshipsFromRemotes()
    {
        // TBD
    }

    // Communicates with Remote
    private void pushMoodEventListToRemote()
    {
        // TBD
    }

    private void pushUserListToRemote()
    {
        // TBD
    }

    private void pushRelationshipsToRemotes()
    {
        // TBD
    }

    private void updateAllCachedLists()
    {
        pullMoodEventListFromRemote();
        pullUserListFromRemote();
        pullRelationshipsFromRemotes();
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
