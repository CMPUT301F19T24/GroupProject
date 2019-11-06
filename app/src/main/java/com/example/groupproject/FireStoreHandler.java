package com.example.groupproject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import static com.example.groupproject.SocialSituation.ALONE;
import static com.example.groupproject.SocialSituation.CROWD;
import static com.example.groupproject.SocialSituation.NONE;
import static com.example.groupproject.SocialSituation.WITH_SEVERAL;
import static com.example.groupproject.SocialSituation.WITH_SOMEONE;

class FSHConstructor
{
    // static variable single_instance of type Singleton
    private static FSHConstructor single_instance = null;

    // variable of type String
    public FireStoreHandler fsh;

    // private constructor restricted to this class itself
    private FSHConstructor()
    {
    fsh = new FireStoreHandler();
}

    // static method to create instance of Singleton class
    public static FSHConstructor getInstance()
    {
        if (single_instance == null)
            single_instance = new FSHConstructor();

        return single_instance;
    }
}

class FireStoreHandler {
    // Testing
    private FirestoreTester fst;

    protected ArrayList<MoodEvent> cachedMoodEvents;
    protected ArrayList<User> cachedUsers;
    protected ArrayList<Relationship> cachedRelationship;
    public FireStoreHandler()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        fst = new FirestoreTester();
        updateAllCachedLists();
    }
    // Communicates with Remote
    protected void pullMoodEventListFromRemote()
    {
        // TODO
        cachedMoodEvents = (ArrayList<MoodEvent>) fst.cachedMoodEvents.clone();
    }

    protected void pullUserListFromRemote()
    {
        // TODO
        cachedUsers = (ArrayList<User>) fst.cachedUsers.clone();
    }

    protected void pullRelationshipsFromRemotes()
    {
        // TODO
        cachedRelationship = (ArrayList<Relationship>) fst.cachedRelationship.clone();

    }

    // Communicates with Remote
    private void pushMoodEventListToRemote()
    {
        // TODO
        fst.cachedMoodEvents = (ArrayList<MoodEvent>) cachedMoodEvents.clone();

    }

    private void pushUserListToRemote()
    {
        // TODO
        fst.cachedUsers = (ArrayList<User>) cachedUsers.clone();

    }

    private void pushRelationshipsToRemotes()
    {
        // TODO
        fst.cachedRelationship = (ArrayList<Relationship>) cachedRelationship.clone();
    }

    private void updateAllCachedLists()
    {
        cachedMoodEvents.clear();
        cachedUsers.clear();
        cachedRelationship.clear();

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
        visibleUsers.add(username);

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

    public User getUserObjWIthUsername(String un)
    {
        User rc = null;
        updateAllCachedLists();

        for(User i : cachedUsers)
        {
//            System.out.println(.getUserName());

            if(i.getUserName() == un)
            {
                rc = i;
                break;
            }
        }
        return rc;
    }

    public void editMoodEvent(MoodEvent me)
    {
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName() == me.getOwner().getUserName())
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));
                cachedMoodEvents.add(me);

                pushMoodEventListToRemote();
                break;
            }
        }
    }
    public void deleteMoodEvent(MoodEvent me)
    {
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName() == me.getOwner().getUserName())
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));

                pushMoodEventListToRemote();
                break;
            }
        }
    }

}
