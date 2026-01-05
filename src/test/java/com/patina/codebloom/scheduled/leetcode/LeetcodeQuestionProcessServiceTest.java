package com.patina.codebloom.scheduled.leetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.repos.job.JobRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.config.NoJdaRequired;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"ci", "thread"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LeetcodeQuestionProcessServiceTest extends NoJdaRequired {

    private final JobRepository jobRepository;
    private final LeetcodeQuestionProcessService service;
    private final QuestionRepository questionRepository;
    private Job testJob;
    private Question testQuestion;
    private String mockUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @Autowired
    public LeetcodeQuestionProcessServiceTest(
            final JobRepository jobRepository,
            final LeetcodeQuestionProcessService service,
            final QuestionRepository questionRepository) {
        this.jobRepository = jobRepository;
        this.service = service;
        this.questionRepository = questionRepository;
    }

    @BeforeAll
    void setup() {
        jobRepository.deleteAllJobs();
        String uniqueSubmissionId = "test-submission-" + System.currentTimeMillis();

        testQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("two-sum-test-" + System.currentTimeMillis())
                .questionTitle("Two Sum Test")
                .questionDifficulty(QuestionDifficulty.Easy)
                .questionNumber(1)
                .questionLink("https://leetcode.com/problems/two-sum/")
                .description(
                        "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.")
                .pointsAwarded(100)
                .acceptanceRate(0.8f)
                .submittedAt(java.time.LocalDateTime.now())
                .runtime("3 ms")
                .memory("14.2 MB")
                .code("def twoSum(self, nums, target): # test code")
                .language("python")
                .submissionId(uniqueSubmissionId)
                .build();

        testQuestion = questionRepository.createQuestion(testQuestion);

        testJob = Job.builder()
                .questionId(testQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now().minusHours(1))
                .build();

        jobRepository.createJob(testJob);
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = jobRepository.deleteJobById(testJob.getId())
                && questionRepository.deleteQuestionById(testQuestion.getId());
        if (!isSuccessful) {
            fail("Failed to clean up test job");
        }
        jobRepository.deleteAllJobs();
    }

    @Test
    void serviceValid() {
        assertNotNull(service);
    }

    @Test
    void findIncompleteJobsValid() {
        Question tempQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("find-incomplete-test-" + System.currentTimeMillis())
                .questionTitle("Find Incomplete Test")
                .questionDifficulty(QuestionDifficulty.Medium)
                .questionNumber(2)
                .questionLink("https://leetcode.com/problems/find-incomplete-test/")
                .description("Test question for incomplete jobs test")
                .pointsAwarded(150)
                .acceptanceRate(0.6f)
                .submittedAt(java.time.LocalDateTime.now())
                .submissionId("test-submission-" + System.currentTimeMillis() + "-456")
                .build();

        tempQuestion = questionRepository.createQuestion(tempQuestion);

        Job freshJob = Job.builder()
                .questionId(tempQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now().minusHours(1))
                .build();

        jobRepository.createJob(freshJob);

        List<Job> incompleteJobs = jobRepository.findIncompleteJobs(10);

        assertNotNull(incompleteJobs);
        assertTrue(incompleteJobs.size() >= 1);
        assertTrue(incompleteJobs.contains(freshJob));
        jobRepository.deleteJobById(freshJob.getId());
        questionRepository.deleteQuestionById(tempQuestion.getId());
    }

    @Test
    void jobStatusTransitionValid() {
        Question tempQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("valid-parentheses-" + System.currentTimeMillis())
                .questionTitle("Valid Parentheses Test")
                .questionDifficulty(QuestionDifficulty.Easy)
                .questionNumber(20)
                .questionLink("https://leetcode.com/problems/valid-parentheses/")
                .description("Test question for job status transition")
                .pointsAwarded(120)
                .acceptanceRate(0.7f)
                .submittedAt(java.time.LocalDateTime.now())
                .submissionId("test-submission-" + System.currentTimeMillis() + "-789")
                .build();

        tempQuestion = questionRepository.createQuestion(tempQuestion);

        Job processingJob = Job.builder()
                .questionId(tempQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now().minusHours(1))
                .build();

        jobRepository.createJob(processingJob);

        assertTrue(processingJob != null);
        assertTrue(processingJob.getId() != null);
        assertEquals(JobStatus.INCOMPLETE, processingJob.getStatus());

        processingJob.setStatus(JobStatus.PROCESSING);
        boolean updateSuccess = jobRepository.updateJob(processingJob);
        assertTrue(updateSuccess);

        Job updatedJob = jobRepository.findJobById(processingJob.getId());
        assertTrue(updatedJob != null);
        assertEquals(JobStatus.PROCESSING, updatedJob.getStatus());

        jobRepository.deleteJobById(processingJob.getId());
        questionRepository.deleteQuestionById(tempQuestion.getId());
    }

    @Test
    void drainQueueValid() {
        service.drainQueue();
    }

    @Test
    void maxAttemptsReached() {

        Question tempQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("max-attempts-test-" + System.currentTimeMillis())
                .questionTitle("Max Attempts Test")
                .questionDifficulty(QuestionDifficulty.Easy)
                .questionNumber(99)
                .questionLink("https://leetcode.com/problems/max-attempts-test/")
                .description("Test for max attempts sentinel value")
                .pointsAwarded(120)
                .acceptanceRate(0.7f)
                .submittedAt(java.time.LocalDateTime.now())
                .submissionId("99999")
                .build();

        tempQuestion = questionRepository.createQuestion(tempQuestion);

        Job maxAttemptJob = Job.builder()
                .questionId(tempQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now().minusHours(1))
                .build();

        jobRepository.createJob(maxAttemptJob);

        CompletableFuture<Empty> future = service.drainQueue();
        future.join();

        Job updatedJob = jobRepository.findJobById(maxAttemptJob.getId());
        assertNotNull(updatedJob);
        assertEquals(JobStatus.COMPLETE, updatedJob.getStatus());

        Question updatedQuestion = questionRepository.getQuestionById(tempQuestion.getId());
        assertNotNull(updatedQuestion);
        assertEquals("99999", updatedQuestion.getSubmissionId());

        jobRepository.deleteJobById(maxAttemptJob.getId());
        questionRepository.deleteQuestionById(tempQuestion.getId());
    }
}
