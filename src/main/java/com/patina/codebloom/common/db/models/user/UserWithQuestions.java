package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.usertag.UserTag;

public class UserWithQuestions extends User {
    private ArrayList<Question> questions;

    public UserWithQuestions(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, tags);
        this.questions = new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(final Question question) {
        questions.add(question);
    }
}
