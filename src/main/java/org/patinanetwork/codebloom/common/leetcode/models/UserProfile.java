package org.patinanetwork.codebloom.common.leetcode.models;

public class UserProfile {

    private final String username;
    private final String ranking;
    private final String userAvatar;
    private final String realName;
    private final String aboutMe;

    public UserProfile(
            final String username,
            final String ranking,
            final String userAvatar,
            final String realName,
            final String aboutMe) {
        this.username = username;
        this.ranking = ranking;
        this.userAvatar = userAvatar;
        this.realName = realName;
        this.aboutMe = aboutMe;
    }

    public String getUsername() {
        return username;
    }

    public String getRanking() {
        return ranking;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getRealName() {
        return realName;
    }

    public String getAboutMe() {
        return aboutMe;
    }
}
