/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
            throw new IllegalArgumentException("tweets is empty.");
        }
        Instant start = tweets.get(0).getTimestamp();
        Instant end = start;
        //find the earliest one and latest one.make them be the timespan`s start and end.
        for (int i = 1; i < tweets.size(); i++) {
            if (tweets.get(i).getTimestamp().isBefore(start)) {
                start = tweets.get(i).getTimestamp();
            }
            if (tweets.get(i).getTimestamp().isAfter(end)) {
                end = tweets.get(i).getTimestamp();
            }
        }
        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> res = new java.util.HashSet<>(Set.of());
        for (Tweet tweet : tweets) {
            String text = tweet.getText();
            boolean isUsername = false;
            List<Character> username = new LinkedList<>();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (isUsername) {
                    if (isLegal(c)) {

                        //if char is upper case, translate it to lower case
                        if (Character.isUpperCase(c)) {
                            c = Character.toLowerCase(c);
                        }
                        username.add(c);
                    } else if (c == ' ') {
                        //imply username meet an end
                        isUsername = false;
                        //translate list to string
                        String usernameStr = username.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining());
                        res.add(usernameStr);
                        username.clear();
                    } else {
                        //username contain illegal character. dont save it.
                        isUsername = false;
                        username.clear();
                    }
                } else if (c == '@') {
                    //imply follow chars is username
                    isUsername = true;
                }
            }
            //handle when username in the end of tweet.
            if (isUsername) {
                //translate list to string
                String usernameStr = username.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining());
                res.add(usernameStr);
            }
        }
        return res;
    }

    private static boolean isLegal(char c) {
        return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_') || (c == '-') || (c >= '0' && c <= '9'));
    }

}
