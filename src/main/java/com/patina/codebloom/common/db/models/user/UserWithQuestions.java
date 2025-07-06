package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.usertag.UserTag;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserWithQuestions extends User {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ArrayList<Question> questions;

    public UserWithQuestions(final String id, final String discordId, final String discordName, final String leetcodeUsername, final String nickname, final Boolean admin,
                    final ArrayList<UserTag> tags) {
        super(id, discordId, discordName, leetcodeUsername, nickname, admin, tags);
        this.questions = new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(final Question question) {
        questions.add(question);
    }
}
