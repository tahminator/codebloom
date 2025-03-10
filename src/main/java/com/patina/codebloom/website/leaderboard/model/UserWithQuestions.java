package com.patina.codebloom.website.leaderboard.model;

import java.util.ArrayList;

import com.patina.codebloom.website.auth.model.User;
import com.patina.codebloom.website.leetcode.models.Question;

public class UserWithQuestions extends User {
    private ArrayList<Question> questions;

    public UserWithQuestions(final String id, final String discordId, final String discordName, final String leetcodeUsername,
                    final String nickname) {
        super(id, discordId, discordName, leetcodeUsername, nickname);
        this.questions = new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(final Question question) {
        questions.add(question);
    }
}
