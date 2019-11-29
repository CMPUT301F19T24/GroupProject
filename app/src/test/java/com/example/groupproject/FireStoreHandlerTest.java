package com.example.groupproject;

import com.example.groupproject.data.firestorehandler.FireStoreHandler;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.data.moods.Angry;
import com.example.groupproject.data.moods.Anxious;
import com.example.groupproject.data.moods.Disgusted;
import com.example.groupproject.data.moods.Happy;
import com.example.groupproject.data.moods.Sad;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;
import com.example.groupproject.data.user.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.example.groupproject.data.relations.SocialSituation.ALONE;
import static com.example.groupproject.data.relations.SocialSituation.CROWD;
import static com.example.groupproject.data.relations.SocialSituation.NONE;
import static com.example.groupproject.data.relations.SocialSituation.WITH_SEVERAL;
import static com.example.groupproject.data.relations.SocialSituation.WITH_SOMEONE;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class FireStoreHandlerTest {
    public FireStoreHandlerTester fsh;

    static String UN_LUKE = "Luke";
    static String UN_LEIA = "Leia";
    static String UN_HANS = "Hans";
    static String UN_OBI_WAN = "ObiWan";

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

    private void initializeTestCase()
    {
        java.util.ArrayList<MoodEvent> arr_me = new ArrayList<>();
        ArrayList<User> arr_u = new ArrayList<>();
        ArrayList<Relationship> arr_rs = new ArrayList<>();

        fsh = new FireStoreHandlerTester();

        // Population
        arr_me.add(new MoodEvent(new Happy(), new GregorianCalendar(2000,01,01), new User(UN_LUKE), ALONE, "1", null, null));
        arr_me.add(new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User(UN_LEIA), WITH_SEVERAL, "2", null, null));
        arr_me.add(new MoodEvent(new Angry(), new GregorianCalendar(2002,01,01), new User(UN_HANS), WITH_SOMEONE, "3", null, null));
        arr_me.add(new MoodEvent(new Anxious(), new GregorianCalendar(2003,01,01), new User(UN_OBI_WAN), CROWD, "4", null, null));


        arr_u.add(new User(UN_LUKE));
        arr_u.add(new User(UN_LEIA));
        arr_u.add(new User(UN_HANS));
        arr_u.add(new User(UN_OBI_WAN));

        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_LEIA), RelationshipStatus.INVISIBLE));
        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_HANS), RelationshipStatus.PENDING));
        arr_rs.add(new Relationship(new User(UN_LUKE), new User(UN_OBI_WAN), RelationshipStatus.FOLLOWING));

        fsh.injectTestData(arr_me, arr_u, arr_rs);

    }

    @Test
    public void test_able_to_see_followers()
    {
        initializeTestCase();
        fsh.getAllCachedMoodEvents();
    }


}