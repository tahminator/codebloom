package com.patina.codebloom.common.db.repos.question.questionbank;

import java.util.ArrayList;
import java.util.List;

import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;



public interface QuestionBankRepository {
    QuestionBank createQuestion(QuestionBank question);

    QuestionBank getQuestionById(String id);
    
    QuestionBank getQuestionBySlug(String slug);
    
    QuestionBank updateQuestion(QuestionBank question);
    
    boolean deleteQuestionById(String id);

    ArrayList<QuestionBank> getAllIncompleteQuestions();

    QuestionBank getRandomQuestion();

    List<QuestionBank> getQuestionsByTopic(QuestionTopic topic);

    List<QuestionBank> getQuestionsByDifficulty(QuestionDifficulty difficulty);

}
