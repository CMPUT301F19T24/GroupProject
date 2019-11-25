package com.example.groupproject;

import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.data.moods.Angry;
import com.example.groupproject.data.moods.Happy;
import com.example.groupproject.data.moods.Sad;
import com.example.groupproject.data.user.User;
import com.example.groupproject.ui.moodlists.SortingMethod;

import org.junit.Test;

import java.util.GregorianCalendar;

import static com.example.groupproject.data.relations.SocialSituation.NONE;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MoodEventTest {
    @Test
    public void test_compareTo()
    {
        MoodEvent a = new MoodEvent(new Angry(), new GregorianCalendar(2001,01,01), new User("A"), NONE, " ", null, null);
        MoodEvent b = new MoodEvent(new Happy(), new GregorianCalendar(2002,01,01), new User("B"), NONE, " ", null, null);
        MoodEvent c = new MoodEvent(new Sad(), new GregorianCalendar(2001,01,01), new User("C"), NONE, " ", null, null);

        assert(a.compareTo(b, SortingMethod.DATE) <= -1); // 2001 < 2002
        assert(a.compareTo(b,SortingMethod.NAME) <= -1); // Angry < Happy

        assert(b.compareTo(a,SortingMethod.DATE) >= 1); // 2002 > 2001
        assert(b.compareTo(a,SortingMethod.NAME) >= 1); // Happy > Angry

        assert(a.compareTo(c,SortingMethod.DATE) == 0); // 2001 == 2001
        assert(a.compareTo(c,SortingMethod.NAME) <= -1); // Angry < Sad

        assert(c.compareTo(a,SortingMethod.DATE) == 0); // 2001 == 2001
        assert(c.compareTo(a,SortingMethod.NAME) >= 1); // Sad > Angry
    }

    @Test
    public void test_contains()
    {
        MoodEvent me = new MoodEvent(new Angry(), new GregorianCalendar(2001,9,11), new User("Billy-Bob"), NONE, " ", null, null);

        assert(me.contains("2001"));
        assert(me.contains("2001 09"));
        assert(me.contains("2001 09 01"));
        assert(me.contains("01 09 2001"));
        assert(!me.contains("2001-09-01")); // Unsuported
        assert(me.contains("Billy"));
        assert(me.contains("Billy-Bob"));
        assert(me.contains("Bob"));
        assert(me.contains("Angry"));
        assert(!me.contains("Sad"));
        assert(me.contains("An Bo 20"));

    }
}