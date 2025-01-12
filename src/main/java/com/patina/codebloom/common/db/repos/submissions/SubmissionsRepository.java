package com.patina.codebloom.common.db.repos.submissions;

import java.util.List;

import com.patina.codebloom.common.db.models.Submission;

public interface SubmissionsRepository {
    List<Submission> findSubmissionsByUsername(String leetcodeUsername);

    List<Submission> findQuestionBySlug(String questionSlug);

    void insertSubmission(Submission submission);
}
