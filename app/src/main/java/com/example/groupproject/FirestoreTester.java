/**
 * This is a temporary testing tool used to emulate the remote.
 */

package com.example.groupproject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.example.groupproject.SocialSituation.ALONE;
import static com.example.groupproject.SocialSituation.CROWD;
import static com.example.groupproject.SocialSituation.NONE;
import static com.example.groupproject.SocialSituation.WITH_SEVERAL;
import static com.example.groupproject.SocialSituation.WITH_SOMEONE;

public class FirestoreTester {
    private static final String UN_LUKE = "Luke Skywalker";
    private static final String UN_LEIA = "Leia Organa";
    private static final String UN_HANS = "Han Solo";
    private static final String UN_OBI_WAN = "Obi Wan";
    private static final String UN_DARTH_VADER = "Darth Vader";

    public ArrayList<MoodEvent> cachedMoodEvents;
    public ArrayList<User> cachedUsers;
    public ArrayList<Relationship> cachedRelationship;
    public FirestoreTester()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        // TODO : REMOVE ME
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2001,01,01), new User(UN_LUKE), ALONE, "Womp-rats", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_LUKE), WITH_SOMEONE, "Lost Hand", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2003,01,01), new User(UN_LUKE), NONE, "Hans + Leia", null, null));
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,01,01), new User(UN_LUKE), WITH_SEVERAL, "Death Star", null, null));

        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), CROWD, "Capture", null, null));
        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2002,01,01), new User(UN_LEIA), CROWD, "Death Star", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2004,01,01), new User(UN_LEIA), WITH_SOMEONE, "Jabba", null, null));

        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_HANS), ALONE, "Carbonite", null, null));

        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2001,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "Qui-Gon", null, null));
        cachedMoodEvents.add(new MoodEvent(new Anxious(), new GregorianCalendar(2002,1,1), new User(UN_OBI_WAN), WITH_SOMEONE, "High Ground", null, null));

        cachedMoodEvents.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2001,1,1), new User(UN_DARTH_VADER), ALONE, "Shimi", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,1,1), new User(UN_DARTH_VADER), CROWD, "Men, Women, Children", null, null));
        cachedMoodEvents.add(new MoodEvent(new Angry(), new GregorianCalendar(2003,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "High Ground", null, null));
        cachedMoodEvents.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,1,1), new User(UN_DARTH_VADER), WITH_SEVERAL, "Killing Palpatine", null, null));
        cachedMoodEvents.add(new MoodEvent(new Sad(), new GregorianCalendar(2005,1,1), new User(UN_DARTH_VADER), WITH_SOMEONE, "Dieing", null, null));

        cachedUsers.add(new User(UN_LUKE));
        cachedUsers.add(new User(UN_LEIA));
        cachedUsers.add(new User(UN_HANS));
        cachedUsers.add(new User(UN_DARTH_VADER));
        cachedUsers.add(new User(UN_OBI_WAN));

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
}
