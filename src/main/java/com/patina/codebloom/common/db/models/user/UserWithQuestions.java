package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.question.Question;

public class UserWithQuestions extends User {
    private ArrayList<Question> questions;

    public UserWithQuestions(String id, String discordId, String discordName, String leetcodeUsername) {
        super(id, discordId, discordName, leetcodeUsername);
        this.questions = new ArrayList<>();
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
