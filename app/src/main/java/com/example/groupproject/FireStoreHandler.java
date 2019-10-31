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
    private static final String UN_LUKE = "Luke Skywalker";
    private static final String UN_LEIA = "Leia Organa";
    private static final String UN_HANS = "Han Solo";
    private static final String UN_OBI_WAN = "Obi Wan";
    private static final String UN_DARTH_VADER = "Darth Vader";

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
        // TODO : REMOVE ME
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2001,01,01), new User(UN_LUKE), NONE, "Womp-rats", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_LUKE), NONE, "Lost Hand", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2003,01,01), new User(UN_LUKE), NONE, "Hans + Leia", null, null));
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,01,01), new User(UN_LUKE), NONE, "Death Star", null, null));

        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), NONE, "Capture", null, null));
        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2002,01,01), new User(UN_LEIA), NONE, "Death Star", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2004,01,01), new User(UN_LEIA), NONE, "Jabba", null, null));

        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_HANS), NONE, "Carbonite", null, null));

        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2001,1,1), new User(UN_OBI_WAN), NONE, "Qui-Gon", null, null));
        cachedMoodEvents.add(new MoodEvent(new Anxious(), new GregorianCalendar(2002,1,1), new User(UN_OBI_WAN), NONE, "High Ground", null, null));

        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2001,1,1), new User(UN_DARTH_VADER), NONE, "Shimi", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,1,1), new User(UN_DARTH_VADER), NONE, "Men, Women, Children", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2003,1,1), new User(UN_DARTH_VADER), NONE, "High Ground", null, null));
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,1,1), new User(UN_DARTH_VADER), NONE, "Killing Palpatine", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2005,1,1), new User(UN_DARTH_VADER), NONE, "Dieing", null, null));

    }

    protected void pullUserListFromRemote()
    {
        // TODO : REMOVE ME
        cachedUsers.add(new User(UN_LUKE));
        cachedUsers.add(new User(UN_LEIA));
        cachedUsers.add(new User(UN_HANS));
        cachedUsers.add(new User(UN_DARTH_VADER));
        cachedUsers.add(new User(UN_OBI_WAN));

    }

    protected void pullRelationshipsFromRemotes()
    {
        // TODO : REMOVE ME
        cachedRelationship.add(new Relationship(new User(UN_LUKE), new User(UN_LEIA), RelationshipStatus.VISIBLE));
        cachedRelationship.add(new Relationship(new User(UN_LUKE), new User(UN_HANS), RelationshipStatus.PENDING_VISIBLE));
        cachedRelationship.add(new Relationship(new User(UN_LUKE), new User(UN_OBI_WAN), RelationshipStatus.FOLLOWING));
        cachedRelationship.add(new Relationship(new User(UN_LUKE), new User(UN_DARTH_VADER), RelationshipStatus.INVISIBLE));

        cachedRelationship.add(new Relationship(new User(UN_LEIA), new User(UN_LUKE), RelationshipStatus.VISIBLE));
        cachedRelationship.add(new Relationship(new User(UN_LEIA), new User(UN_DARTH_VADER), RelationshipStatus.FOLLOWING));
        cachedRelationship.add(new Relationship(new User(UN_LEIA), new User(UN_HANS), RelationshipStatus.FOLLOWING));
        // Leia -> Obi-Wan left out

        cachedRelationship.add(new Relationship(new User(UN_HANS), new User(UN_LEIA), RelationshipStatus.PENDING_FOLLOWING));
        cachedRelationship.add(new Relationship(new User(UN_HANS), new User(UN_LUKE), RelationshipStatus.VISIBLE));
        // Hans -> Obi-Wan left out
        // Hans-> Darth Vader left out

        cachedRelationship.add(new Relationship(new User(UN_OBI_WAN), new User(UN_LUKE), RelationshipStatus.FOLLOWING));
        cachedRelationship.add(new Relationship(new User(UN_OBI_WAN), new User(UN_LEIA), RelationshipStatus.VISIBLE));
        cachedRelationship.add(new Relationship(new User(UN_OBI_WAN), new User(UN_HANS), RelationshipStatus.INVISIBLE));
        cachedRelationship.add(new Relationship(new User(UN_OBI_WAN), new User(UN_DARTH_VADER), RelationshipStatus.FOLLOWING));

        cachedRelationship.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_LUKE), RelationshipStatus.PENDING_FOLLOWING));
        cachedRelationship.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_LEIA), RelationshipStatus.PENDING_VISIBLE));
        // Darth Vader -> Hans left out
        cachedRelationship.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_OBI_WAN), RelationshipStatus.INVISIBLE));

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


}
