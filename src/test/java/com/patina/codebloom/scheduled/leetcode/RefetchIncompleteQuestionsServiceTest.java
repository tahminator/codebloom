package com.patina.codebloom.scheduled.leetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.repos.job.JobRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.config.NoJdaRequired;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RefetchIncompleteQuestionsServiceTest extends NoJdaRequired {

    private final QuestionRepository questionRepository;
    private final JobRepository jobRepository;
    private final RefetchIncompleteQuestionsService service;

    private Question testQuestion;
    private final String mockUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @Autowired
    public RefetchIncompleteQuestionsServiceTest(
            final QuestionRepository questionRepository,
            final JobRepository jobRepository,
            final RefetchIncompleteQuestionsService service) {
        this.questionRepository = questionRepository;
        this.jobRepository = jobRepository;
        this.service = service;
    }

    @BeforeAll
    void setup() {
        jobRepository.deleteAllJobs();

        testQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("base-question-" + System.currentTimeMillis())
                .questionTitle("Base Question")
                .questionDifficulty(QuestionDifficulty.Easy)
                .questionNumber(100)
                .questionLink("https://leetcode.com/problems/base-question/")
                .description("Baseline question for testing")
                .pointsAwarded(50)
                .language(null)
                .acceptanceRate(0.9f)
                .submittedAt(java.time.LocalDateTime.now())
                .build();

        testQuestion = questionRepository.createQuestion(testQuestion);

        Job existingJob = Job.builder()
                .questionId(testQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now())
                .build();

        jobRepository.createJob(existingJob);
    }

    @AfterAll
    void cleanup() {
        jobRepository.deleteAllJobs();
        questionRepository.deleteQuestionById(testQuestion.getId());
    }

    @Test
    void serviceValid() {
        assertNotNull(service);
    }

    @Test
    void refetchIncompleteQuestionsValid() {
        Question orphanedQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("orphaned-test-" + System.currentTimeMillis())
                .questionTitle("Orphaned Question")
                .questionDifficulty(QuestionDifficulty.Medium)
                .questionNumber(200)
                .questionLink("https://leetcode.com/problems/orphaned-test/")
                .description("This question has no job and should be picked up.")
                .pointsAwarded(100)
                .acceptanceRate(0.75f)
                .submittedAt(java.time.LocalDateTime.now())
                .build();

        questionRepository.createQuestion(orphanedQuestion);

        service.refetchIncompleteQuestions();

        List<Job> incompleteJobs = jobRepository.findIncompleteJobs(50);

        Job createdJob = incompleteJobs.stream()
                .filter(job -> job.getQuestionId().equals(orphanedQuestion.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdJob, "Service should have created a job for the orphaned question");
        assertEquals(JobStatus.INCOMPLETE, createdJob.getStatus());

        jobRepository.deleteJobById(createdJob.getId());
        questionRepository.deleteQuestionById(orphanedQuestion.getId());
    }

    @Test
    void handleDuplicateKeyError() {

        Question collisionQuestion = Question.builder()
                .userId(mockUserId)
                .questionSlug("collision")
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
                .language(null)
                .submissionId("test-submission-1234")
                .build();

        questionRepository.createQuestion(collisionQuestion);

        Job firstJob = Job.builder()
                .questionId(collisionQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .build();
        jobRepository.createJob(firstJob);

        // make second job with same id
        Job secondJob = Job.builder()
                .questionId(collisionQuestion.getId())
                .status(JobStatus.INCOMPLETE)
                .build();

        jobRepository.createJob(secondJob);

        List<Job> jobs = jobRepository.findJobsByQuestionId(collisionQuestion.getId());
        assertNotNull(jobs);
        assertEquals(2, jobs.size());

        jobRepository.deleteJobById(firstJob.getId());
        jobRepository.deleteJobById(secondJob.getId());
        questionRepository.deleteQuestionById(collisionQuestion.getId());
    }
}
