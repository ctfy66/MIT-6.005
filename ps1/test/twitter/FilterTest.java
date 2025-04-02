/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */

    /*
        test writtenBy strategy:
        amount of tweets : 0, 1, >1
        amount of result : 0, 1, >1
        special case : username whether is case-insensitive
     */

    /*
        test inTimespan strategy:
        amout of tweets : 0, >0
        amount of result : 0, 1, >1

     */

    /*
        test containing strategy:
        amount of tweets : 0, 1, >1
        amount of word : 0, 1, >1
        amount of result: 0, >0
        special case :
        1. case-insensitive
        2. target word position: head, middle, tail
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to Talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "rivest talk in 30 minutes #hype", d2);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(new ArrayList<>(), "me");
        assertTrue("expected empty list", writtenBy.isEmpty());
    }

    @Test
    public void testWrittenBySingleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");

        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByMultipleTweetsMultipleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");

        assertEquals("expected two-entry list", 2, writtenBy.size());
        assertEquals("first tweet is tweet1", writtenBy.get(0), tweet1);
        assertEquals("second tweet is tweet3", writtenBy.get(1), tweet3);

    }
    @Test
    public void testWrittenByMultipleTweetsEmptyResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "me");
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "AlysSa");

        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testInTimespanEmptyTweetsEmptyResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        assertTrue("expect an empty list", inTimespan.isEmpty());
    }

    @Test
    public void testInTimespanMultipleTweetsEmptyResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:30:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    @Test
    public void testInTimespanMultipleTweetsSingleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertEquals("expected one-entry list", 1, inTimespan.size());
        assertTrue("expected list to contain right tweet", inTimespan.contains(tweet1));
    }


        @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    


    @Test
    public void testContainingEmptyTweetsEmptyWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList());

        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingEmptyTweetsNonEmptyWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList("talk"));

        assertTrue("expected empty list", containing.isEmpty());
    }
    @Test
    public void testContainingNonEmptyTweetsEmptyWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList());

        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingSingleTweetsSingleWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("minute"));

        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingSingleTweetsSingleWordsSingleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingSingleTweetsMultipleWordsSingleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk", "minute"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingMultipleTweetsSingleWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("minute"));

        assertTrue("expected empty list", containing.isEmpty());
    }
    @Test
    public void testContainingMultipleTweetsSingleWordsSingleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("reasonable"));

        assertEquals("expected one-entry list", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsSingleWordsMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsMultipleWordsEmptyResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("minute, time"));
        assertTrue("expected empty list", containing.isEmpty());
    }
    @Test
    public void testContainingMultipleTweetsMultipleWordsSingleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("minute","reasonable"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsMultipleWordsMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("reasonable", "minutes"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("TaLk"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingAtHead() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("is"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingAtMiddle() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("reasonable"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingAtTail() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("much?"));

        assertEquals("expected list contain one entry", 1, containing.size());
        assertTrue("expected contain right tweet", containing.contains(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
