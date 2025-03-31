/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.sql.Time;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    /*
    * getTimespan test strategies:
    * tweets.length() : 0, 1, >1
    * include condition that all tweets send on the same time.
    * getMentionedUsers test strategies:
    *用户名出现位置：开头，中间，末尾
    * 推文用户名出现个数：0， 1， >1
    * 多个相同用户名被提及:一条推文, 大于一条推文
    * 用户名包含非法字符
    * 用户名大写与小写是否识别成一个.
    */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "bbitdiddle", "rivest talk in 30 minutes #hype @alyssa", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTimespanEmpty() {
        Timespan timespan = Extract.getTimespan(List.of());
    }
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(List.of(tweet1));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    @Test
    public void testGetTimespanThreeTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersAtHead() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "@user1 hi", d1)));
        System.out.println("Mentioned users: " + mentionedUsers);
        for (String user : mentionedUsers) {
            System.out.println("User: '" + user + "'");
        }
        assertTrue("expected one entry set", mentionedUsers.size() == 1);
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }
    @Test
    public void testGetMentionedUsersAtMiddle() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1 how are you?", d1)));

        assertTrue("expected one entry set", mentionedUsers.size() == 1);
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }

    @Test
    public void testGetMentionedUsersAtTail() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1", d1)));

        assertEquals("expected one entry set", 1, mentionedUsers.size());
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }
    @Test
    public void testMentionedUserMoreThanOnceInOneTweet() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1 what the hell?@user1", d1)));
        assertEquals("expected one entry set", 1, mentionedUsers.size());
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }
    @Test
    public void testMentionedUserMoreThanOnceInMultipleTweets() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1 what the hell?", d1), new Tweet(1, "user2", "hi, @user1. what the hell?", d1)));
        assertEquals("expected one entry set", 1, mentionedUsers.size());
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }

    @Test
    public void testUsernameContainIllegalCharacter() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1.jlu.edu", d1)));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    @Test
    public void testUsernameCaseInsensitive() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(new Tweet(1, "user1", "hi, @user1", d1), new Tweet(1, "user2", "hi, @UsEr1. what the hell?", d1)));

        assertEquals("expected one entry set", 1, mentionedUsers.size());
        assertTrue("expected mentioned user is user1", mentionedUsers.contains("user1"));
    }
    @Test
    public void testGetMentionedUsersEmptyList() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of());
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
