package com.patina.codebloom.common.test;

import java.util.ArrayList;
import java.util.Comparator;

import com.github.javafaker.Faker;
import com.patina.codebloom.common.test.models.UserAndMetadata;

public class TestLeaderboardList {
    private final static Faker faker = new Faker();

    public static ArrayList<UserAndMetadata> getTestDataShallowList() {
        ArrayList<UserAndMetadata> leaderboard = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            leaderboard.add(new UserAndMetadata(faker.name().username(), faker.name().username(),
                    faker.number().numberBetween(0, 10000)));
        }

        leaderboard.sort(Comparator.comparingInt(UserAndMetadata::getTotalScore).reversed());

        return leaderboard;
    }

    public static ArrayList<UserAndMetadata> getTestDataLongList() {
        ArrayList<UserAndMetadata> leaderboard = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            leaderboard.add(new UserAndMetadata(faker.name().username(), faker.name().username(),
                    faker.number().numberBetween(0, 10000)));
        }

        leaderboard.sort(Comparator.comparingInt(UserAndMetadata::getTotalScore).reversed());

        return leaderboard;
    }
}
