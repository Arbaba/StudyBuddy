package ch.epfl.sweng.studdybuddy;

import org.junit.Test;

import ch.epfl.sweng.studdybuddy.core.Buddy;
import ch.epfl.sweng.studdybuddy.core.Pair;
import ch.epfl.sweng.studdybuddy.util.Helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BuddyTest {
    @Test
    public void testSettersGetters() {
        Buddy b = new Buddy("alice", "bob");
        b.setAlice("bob");
        b.setBob("alice");
        b.setCreationDate((long)0);
        assertEquals("alice", b.getBob());
        assertEquals("bob", b.getAlice());
        assertEquals(new Long(0), b.getCreationDate());
    }

    @Test
    public void testBuddyOf() {
        Buddy b = new Buddy("alice", "bob");
        assertEquals("bob", b.buddyOf("alice"));
        assertEquals("alice", b.buddyOf("bob"));
        assertNull(b.buddyOf("eve"));
    }
    @Test
    public void testHash() {
        Buddy b = new Buddy("alice", "bob");
        Buddy b2 = new Buddy("bob", "alice");
        assertEquals(Integer.toHexString("alice".hashCode() ^ "bob".hashCode()), b.hash());
        assertEquals(b.hash(), b2.hash());
    }
}