/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    /*
        test guessFollowsGraph strategy:
        amount of tweets : 0, 1, >1
        amount of key in map : 0, 1, >1
        amount of entry in set: 1, >1
     */

    /*
        test influencer strategy:
        amount of keys : 0, 1, >1
        special case:
        some counts are equal
     */
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to Talk about rivest so much @bbitdiddle ?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "hui", "rivest talk @alyssa @bbitdiddle in 30 minutes #hype @alyssa", d2);
    private static final Tweet tweet4 = new Tweet(4, "alyssa", "is it reasonable to Talk about rivest so much @bbitdiddle ?", d1);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowGraphSingleTweetSingleKeyEmptySet() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(tweet2)));
        assertTrue("expected empty graph or set is empty", (followsGraph.isEmpty() || followsGraph.get(tweet2.getAuthor().toLowerCase()).isEmpty()));
    }
    @Test
    public void testGuessFollowGraphSingleTweetSingleKeyOneEntrySet() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(tweet1)));
        assertTrue("expected right key-value pair", followsGraph.get(tweet1.getAuthor().toLowerCase()).contains("bbitdiddle") && followsGraph.get(tweet1.getAuthor().toLowerCase()).size() == 1);
    }
    @Test
    public void testGuessFollowGraphSingleTweetSingleKeyMultipleEntrySet() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(tweet3)));
        assertTrue("expected right key-value pair", followsGraph.get(tweet3.getAuthor().toLowerCase()).containsAll(Arrays.asList("bbitdiddle", "alyssa")) && followsGraph.get(tweet3.getAuthor().toLowerCase()).size() == 2);
    }

    @Test
    public void testGuessFollowGraphMultipleTweetsSingleKeyOneEntrySet() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(tweet1, tweet4)));
        assertTrue("expected right key-value pair", followsGraph.get(tweet1.getAuthor().toLowerCase()).contains("bbitdiddle") && followsGraph.get(tweet1.getAuthor().toLowerCase()).size() == 1);
    }

    @Test
    public void testGuessFollowGraphMultipleTweetsSingleKeyMultipleEntrySet() {
        final Tweet t1 = new Tweet(1, "alyssa", "is it reasonable to Talk about rivest so much @bbitdiddle ?", d1);
        final Tweet t2 = new Tweet(1, "alyssa", "is it reasonable to Talk about rivest so much @bit ?", d1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(t1, t2)));
        assertTrue("expected right key-value pair", followsGraph.get("alyssa").containsAll(Arrays.asList("bbitdiddle", "bit")) && followsGraph.get("alyssa").size() == 2);
    }
    @Test
    public void testGuessFollowGraphMultipleTweetsMultipleKeyMultipleEntrySet() {
        final Tweet t1 = new Tweet(1, "alyssa", "is it reasonable to Talk about rivest so much @bit @bbitdiddle ?", d1);
        final Tweet t2 = new Tweet(1, "bit", "is it reasonable to Talk about rivest so much @alyssa ?", d1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>(Arrays.asList(t1, t2)));
        assertTrue("expected right key-value pair", followsGraph.get("alyssa").containsAll(Arrays.asList("bit", "bbitdiddle")) && followsGraph.get("alyssa").size() == 2);
        assertTrue("expected right key-value pair", followsGraph.get("bit").containsAll(Arrays.asList("alyssa")) && followsGraph.get("bit").size() == 1);

    }



    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencerSingle() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("expected two people", 2, influencers.size());
        assertEquals("expected right order", "bbitdiddle", influencers.get(0));
        assertEquals("expected right order", "alyssa", influencers.get(1));
    }

    @Test
    public void testInfluencerMultiple() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet3));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("expected three people", 3, influencers.size());
        assertEquals("expected right order", "bbitdiddle", influencers.get(0));
        assertEquals("expected right order", "alyssa", influencers.get(1));
        assertEquals("expected right order", "hui", influencers.get(2));

    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
