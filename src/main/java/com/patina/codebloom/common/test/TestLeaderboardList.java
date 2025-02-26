package com.patina.codebloom.common.test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import com.github.javafaker.Faker;
import com.patina.codebloom.common.db.models.leaderboard.LeaderboardWithUsers;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public class TestLeaderboardList {
    private static final Faker FAKER = new Faker();

    public static LeaderboardWithUsers getTestDataShallowList() {
        LeaderboardWithUsers leaderboard = null;

        Date birthday = FAKER.date().birthday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String randomDate = dateFormat.format(birthday);

        ArrayList<UserWithScore> users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            boolean addNickname = ThreadLocalRandom.current().nextInt(0, 2) % 2 == 0;
            String nickname = addNickname ? FAKER.name().username() : null;
            users.add(new UserWithScore(FAKER.internet().uuid(), FAKER.number().digits(20), FAKER.name().username(), FAKER.name().username(), nickname, FAKER.number().numberBetween(0, 12000)));
        }

        users.sort(Comparator.comparingInt(UserWithScore::getTotalScore).reversed());

        leaderboard = new LeaderboardWithUsers(FAKER.internet().uuid(), String.join(" ", FAKER.lorem().words()), LocalDateTime.parse(randomDate), null, users);

        return leaderboard;
    }

    public static LeaderboardWithUsers getTestDataLongList() {
        LeaderboardWithUsers leaderboard = null;

        Date birthday = FAKER.date().birthday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String randomDate = dateFormat.format(birthday);

        ArrayList<UserWithScore> users = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            boolean addNickname = ThreadLocalRandom.current().nextInt(0, 2) % 2 == 0;
            String nickname = addNickname ? FAKER.name().username() : null;
            users.add(new UserWithScore(FAKER.internet().uuid(), FAKER.number().digits(20), FAKER.name().username(), FAKER.name().username(), nickname, FAKER.number().numberBetween(0, 12000)));
        }

        users.sort(Comparator.comparingInt(UserWithScore::getTotalScore).reversed());

        leaderboard = new LeaderboardWithUsers(FAKER.internet().uuid(), String.join(" ", FAKER.lorem().words()), LocalDateTime.parse(randomDate), null, users);

        return leaderboard;
    }
}
