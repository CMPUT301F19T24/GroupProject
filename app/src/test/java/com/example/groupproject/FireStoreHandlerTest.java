package com.example.groupproject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.example.groupproject.SocialSituation.ALONE;
import static com.example.groupproject.SocialSituation.CROWD;
import static com.example.groupproject.SocialSituation.NONE;
import static com.example.groupproject.SocialSituation.WITH_SEVERAL;
import static com.example.groupproject.SocialSituation.WITH_SOMEONE;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class FireStoreHandlerTest {
    public FireStoreHandlerTester fsh;

    // Test case 1 defs
    private static final int NUM_OF_LUKE_ME = 4;
    private static final int NUM_OF_LEIA_ME = 3;
    private static final int NUM_OF_HANS_ME = 1;
    private static final int NUM_OF_OBI_WAN_ME = 2;
    private static final int NUM_OF_DARTH_VADER_ME = 5;

    private static final String UN_LUKE = "Luke Skywalker";
    private static final String UN_LEIA = "Leia Organa";
    private static final String UN_HANS = "Han Solo";
    private static final String UN_OBI_WAN = "Obi Wan";
    private static final String UN_DARTH_VADER = "Darth Vader";

    private static final String MOOD_NAME_HAPPY = "Happy";
    private static final String MOOD_NAME_SAD = "Sad";
    private static final String MOOD_NAME_ANGRY = "Angry";
    private static final String MOOD_NAME_ANXIOUS= "Anxious";
    private static final String MOOD_NAME_DISGUSTED = "Disgusted";

    private static final int NUM_HAPPY_ME = 3;
    private static final int NUM_SAD_ME = 5;
    private static final int NUM_ANGRY_ME = 4;
    private static final int NUM_ANXIOUS_ME = 1;
    private static final int NUM_DISGUSTED_ME = 2;

    private static final int NUM_HAPPY_LUKE = 2;
    private static final int NUM_SAD_VADER = 1;

    // Test case 2 defs
    private static final String UN_MR_I_HAVE_NO_MOOD_HISTORY = "No Followers";
    private static final String UN_MR_NO_ONE_CAN_SEE_ME = "No Visibles";
    private static final String UN_MR_NO_SEEING_OTHERS = "No Mood History";

    private static final int NUM_MR_I_HAVE_NO_MOOD_HISTORY_ME = 0;
    private static final int NUM_MR_NO_ONE_CAN_SEE_ME_ME = 1;
    private static final int NUM_MR_NO_SEEING_OTHERS_ME = 2;

    public class FireStoreHandlerTester extends FireStoreHandler
    {
        FireStoreHandlerTester()
        {
            super();
        }

        public void injectTestData(ArrayList<MoodEvent> arr_me, ArrayList<User> arr_u, ArrayList<Relationship> arr_rs)
        {
            this.cachedMoodEvents = arr_me;
            this.cachedUsers = arr_u;
            this.cachedRelationship = arr_rs;
        }
    }

    private void initializeTestCase1()
    {
        java.util.ArrayList<MoodEvent> arr_me = new ArrayList<>();
        ArrayList<User> arr_u = new ArrayList<>();
        ArrayList<Relationship> arr_rs = new ArrayList<>();

        fsh = new FireStoreHandlerTester();

        // Population
        arr_me.add(new MoodEvent(new Happy(), new GregorianCalendar(2001,01,01), new User(UN_LUKE), NONE, "Womp-rats", null, null));
        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_LUKE), NONE, "Lost Hand", null, null));
        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2003,01,01), new User(UN_LUKE), NONE, "Hans + Leia", null, null));
        arr_me.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,01,01), new User(UN_LUKE), NONE, "Death Star", null, null));

        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), NONE, "Capture", null, null));
        arr_me.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2002,01,01), new User(UN_LEIA), NONE, "Death Star", null, null));
        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2004,01,01), new User(UN_LEIA), NONE, "Jabba", null, null));

        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2002,01,01), new User(UN_HANS), NONE, "Carbonite", null, null));

        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2001,1,1), new User(UN_OBI_WAN), NONE, "Qui-Gon", null, null));
        arr_me.add(new MoodEvent(new Anxious(), new GregorianCalendar(2002,1,1), new User(UN_OBI_WAN), NONE, "High Ground", null, null));

        arr_me.add(new MoodEvent(new Disgusted(), new GregorianCalendar(2001,1,1), new User(UN_DARTH_VADER), NONE, "Shimi", null, null));
        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,1,1), new User(UN_DARTH_VADER), NONE, "Men, Women, Children", null, null));
        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2003,1,1), new User(UN_DARTH_VADER), NONE, "High Ground", null, null));
        arr_me.add(new MoodEvent(new Happy(), new GregorianCalendar(2004,1,1), new User(UN_DARTH_VADER), NONE, "Killing Palpatine", null, null));
        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2005,1,1), new User(UN_DARTH_VADER), NONE, "Dieing", null, null));

        arr_u.add(new User(UN_LUKE));
        arr_u.add(new User(UN_LEIA));
        arr_u.add(new User(UN_HANS));
        arr_u.add(new User(UN_DARTH_VADER));
        arr_u.add(new User(UN_OBI_WAN));

        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_LEIA), RelationshipStatus.VISIBLE));
        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_HANS), RelationshipStatus.PENDING_VISIBLE));
        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_OBI_WAN), RelationshipStatus.FOLLOWING));
        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_DARTH_VADER), RelationshipStatus.INVISIBLE));

        arr_rs.add(new Relationship(new User(UN_LEIA), new User(UN_LUKE), RelationshipStatus.VISIBLE));
        arr_rs.add(new Relationship(new User(UN_LEIA), new User(UN_DARTH_VADER), RelationshipStatus.FOLLOWING));
        arr_rs.add(new Relationship(new User(UN_LEIA), new User(UN_HANS), RelationshipStatus.FOLLOWING));
        // Leia -> Obi-Wan left out

        arr_rs.add(new Relationship(new User(UN_HANS), new User(UN_LEIA), RelationshipStatus.PENDING_FOLLOWING));
        arr_rs.add(new Relationship(new User(UN_HANS), new User(UN_LUKE), RelationshipStatus.VISIBLE));
        // Hans -> Obi-Wan left out
        // Hans-> Darth Vader left out

        arr_rs.add(new Relationship(new User(UN_OBI_WAN), new User(UN_LUKE), RelationshipStatus.FOLLOWING));
        arr_rs.add(new Relationship(new User(UN_OBI_WAN), new User(UN_LEIA), RelationshipStatus.VISIBLE));
        arr_rs.add(new Relationship(new User(UN_OBI_WAN), new User(UN_HANS), RelationshipStatus.INVISIBLE));
        arr_rs.add(new Relationship(new User(UN_OBI_WAN), new User(UN_DARTH_VADER), RelationshipStatus.FOLLOWING));

        arr_rs.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_LUKE), RelationshipStatus.PENDING_FOLLOWING));
        arr_rs.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_LEIA), RelationshipStatus.PENDING_VISIBLE));
        // Darth Vader -> Hans left out
        arr_rs.add(new Relationship(new User(UN_DARTH_VADER), new User(UN_OBI_WAN), RelationshipStatus.INVISIBLE));

        fsh.injectTestData(arr_me, arr_u, arr_rs);

    }


    private void initializeTestCase2()
    {
        java.util.ArrayList<MoodEvent> arr_me = new ArrayList<>();
        ArrayList<User> arr_u = new ArrayList<>();
        ArrayList<Relationship> arr_rs = new ArrayList<>();

        fsh = new FireStoreHandlerTester();

        // Population
        arr_me.add(new MoodEvent(new Happy(), new GregorianCalendar(2001,01,01), new User(UN_MR_NO_ONE_CAN_SEE_ME), NONE, " ", null, null));
        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_MR_NO_SEEING_OTHERS), NONE, " ", null, null));
        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,01,01), new User(UN_MR_NO_SEEING_OTHERS), NONE, " ", null, null));

        arr_u.add(new User(UN_MR_I_HAVE_NO_MOOD_HISTORY));
        arr_u.add(new User(UN_MR_NO_ONE_CAN_SEE_ME));
        arr_u.add(new User(UN_MR_NO_SEEING_OTHERS));

        arr_rs.add(new Relationship(new User(UN_MR_I_HAVE_NO_MOOD_HISTORY), new User(UN_MR_NO_ONE_CAN_SEE_ME), RelationshipStatus.INVISIBLE));
        arr_rs.add(new Relationship(new User(UN_MR_I_HAVE_NO_MOOD_HISTORY), new User(UN_MR_NO_SEEING_OTHERS), RelationshipStatus.VISIBLE));

        arr_rs.add(new Relationship(new User(UN_MR_NO_ONE_CAN_SEE_ME), new User(UN_MR_I_HAVE_NO_MOOD_HISTORY), RelationshipStatus.VISIBLE));
        arr_rs.add(new Relationship(new User(UN_MR_NO_ONE_CAN_SEE_ME), new User(UN_MR_NO_SEEING_OTHERS), RelationshipStatus.VISIBLE));

        arr_rs.add(new Relationship(new User(UN_MR_NO_SEEING_OTHERS), new User(UN_MR_I_HAVE_NO_MOOD_HISTORY), RelationshipStatus.INVISIBLE));
        arr_rs.add(new Relationship(new User(UN_MR_NO_SEEING_OTHERS), new User(UN_MR_NO_ONE_CAN_SEE_ME), RelationshipStatus.INVISIBLE));

        fsh.injectTestData(arr_me, arr_u, arr_rs);

    }

    @Test
    public void test_getMoodEventsByUsername()
    {
        initializeTestCase1();
        assertEquals(NUM_OF_LUKE_ME, fsh.getMoodEventsByUsername(UN_LUKE).size());
        assertEquals(NUM_OF_LEIA_ME, fsh.getMoodEventsByUsername(UN_LEIA).size());
        assertEquals(NUM_OF_HANS_ME, fsh.getMoodEventsByUsername(UN_HANS).size());
        assertEquals(NUM_OF_OBI_WAN_ME, fsh.getMoodEventsByUsername(UN_OBI_WAN).size());
        assertEquals(NUM_OF_DARTH_VADER_ME, fsh.getMoodEventsByUsername(UN_DARTH_VADER).size());
    }

    @Test
    public void test_getMoodEventsByMoodName()
    {
        initializeTestCase1();
        assertEquals(NUM_HAPPY_ME, fsh.getMoodEventsByMoodName(MOOD_NAME_HAPPY).size());
        assertEquals(NUM_SAD_ME, fsh.getMoodEventsByMoodName(MOOD_NAME_SAD).size());
        assertEquals(NUM_ANGRY_ME, fsh.getMoodEventsByMoodName(MOOD_NAME_ANGRY).size());
        assertEquals(NUM_ANXIOUS_ME, fsh.getMoodEventsByMoodName(MOOD_NAME_ANXIOUS).size());
        assertEquals(NUM_DISGUSTED_ME, fsh.getMoodEventsByMoodName(MOOD_NAME_DISGUSTED).size());
    }

    @Test
    public void test_getMoodEventsByMoodNameAndUserName()
    {
        initializeTestCase1();
        assertEquals(NUM_HAPPY_LUKE, fsh.getMoodEventsByMoodNameAndUserName(MOOD_NAME_HAPPY, UN_LUKE).size());
        assertEquals(NUM_SAD_VADER, fsh.getMoodEventsByMoodNameAndUserName(MOOD_NAME_SAD, UN_DARTH_VADER).size());
    }

    @Test
    public void test_getVisibleMoodEvents()
    {
        initializeTestCase1();
        assertEquals(NUM_OF_LEIA_ME + NUM_OF_OBI_WAN_ME, fsh.getVisibleMoodEvents(UN_LUKE).size());
        assertEquals(NUM_OF_LUKE_ME + NUM_OF_HANS_ME + NUM_OF_DARTH_VADER_ME, fsh.getVisibleMoodEvents(UN_LEIA).size());
        assertEquals(NUM_OF_LUKE_ME + NUM_OF_LEIA_ME, fsh.getVisibleMoodEvents(UN_HANS).size());
        assertEquals(NUM_OF_LUKE_ME + NUM_OF_LEIA_ME + NUM_OF_DARTH_VADER_ME, fsh.getVisibleMoodEvents(UN_OBI_WAN).size());
        assertEquals(NUM_OF_LUKE_ME, fsh.getVisibleMoodEvents(UN_DARTH_VADER).size());
    }

    @Test
    public void test_odd_cases()
    {
        initializeTestCase2();

        assertEquals(NUM_MR_NO_SEEING_OTHERS_ME + 0 /* Can't see MR_NO_ONE_CAN_SEE_ME*/, fsh.getVisibleMoodEvents(UN_MR_I_HAVE_NO_MOOD_HISTORY).size());
        assertEquals(NUM_MR_NO_SEEING_OTHERS_ME + 0 /* No ME for MR_I_HAVE_NO_MOOD_HISTORY*/, fsh.getVisibleMoodEvents(UN_MR_NO_ONE_CAN_SEE_ME).size());
        assertEquals(0 /* Can't see anyone else*/ , fsh.getVisibleMoodEvents(UN_MR_NO_SEEING_OTHERS).size());


    }

}