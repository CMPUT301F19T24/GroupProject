package com.example.groupproject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RelationshipTest {
    @Test
    public void test_isVisible()
    {
        assertTrue(new Relationship(null, null, RelationshipStatus.INVISIBLE).isVisible() == false);
        assertTrue(new Relationship(null, null, RelationshipStatus.PENDING_VISIBLE).isVisible() == false);
        assertTrue(new Relationship(null, null, RelationshipStatus.VISIBLE).isVisible() == true);
        assertTrue(new Relationship(null, null, RelationshipStatus.PENDING_FOLLOWING).isVisible() == true);
        assertTrue(new Relationship(null, null, RelationshipStatus.FOLLOWING).isVisible() == true);

    }
}