package com.example.groupproject;

import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;

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
        assertTrue(new Relationship(null, null, RelationshipStatus.PENDING).isVisible() == false);
        assertTrue(new Relationship(null, null, RelationshipStatus.FOLLOWING).isVisible() == true);

    }
}