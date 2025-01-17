package com.patina.codebloom.common.test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import com.github.javafaker.Faker;
import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public class TestLeaderboardList {
    private final static Faker faker = new Faker();

    public static LeaderboardWithUsers getTestDataShallowList() {
        LeaderboardWithUsers leaderboard = null;

        Date birthday = faker.date().birthday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String randomDate = dateFormat.format(birthday);

        ArrayList<UserWithScore> users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            users.add(new UserWithScore(faker.internet().uuid(), faker.number().digits(20), faker.name().username(),
                    null, faker.number().numberBetween(0, 8000)));
        }

        users.sort(Comparator.comparingInt(UserWithScore::getTotalScore).reversed());

        leaderboard = new LeaderboardWithUsers(faker.internet().uuid(), faker.name().name(),
                LocalDateTime.parse(randomDate),
                null, users);

        return leaderboard;
    }

    public static LeaderboardWithUsers getTestDataLongList() {
        LeaderboardWithUsers leaderboard = null;

        Date birthday = faker.date().birthday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String randomDate = dateFormat.format(birthday);

        ArrayList<UserWithScore> users = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            users.add(new UserWithScore(faker.internet().uuid(), faker.number().digits(20), faker.name().username(),
                    null, faker.number().numberBetween(0, 8000)));
        }

        users.sort(Comparator.comparingInt(UserWithScore::getTotalScore).reversed());

        leaderboard = new LeaderboardWithUsers(faker.internet().uuid(), faker.name().name(),
                LocalDateTime.parse(randomDate),
                null, users);

        return leaderboard;
    }
}
