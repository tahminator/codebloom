
package com.patina.codebloom.scheduled.leetcode;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.repos.job.JobRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!ci")
public class LeetcodeQuestionProcessService {
    private static final int MAX_JOBS_PER_RUN = 10;
    private static final long REQUESTS_OVER_TIME = 1L;
    private static final long MILLISECONDS_TO_WAIT = 100L;

    private final JobRepository jobRepository;
    private final LeetcodeClient leetcodeClient;
    private final QuestionRepository questionRepository;
    private final QuestionTopicRepository questionTopicRepository;
    private final BlockingBucket rateLimiter;

    private BlockingBucket initializeBucket() {
        var bandwidth = Bandwidth.builder()
                        .capacity(REQUESTS_OVER_TIME)
                        .refillIntervally(REQUESTS_OVER_TIME, Duration.ofMillis(MILLISECONDS_TO_WAIT))
                        .build();

        return Bucket.builder()
                        .addLimit(bandwidth)
                        .build().asBlocking();
    }

    private void waitForToken() {
        try {
            rateLimiter.consume(1);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to consume rate limit bucket token in leetcode question process service", e);
        }
    }

    public LeetcodeQuestionProcessService(final JobRepository jobRepository,
                    final ThrottledLeetcodeClient throttledLeetcodeClient,
                    final QuestionRepository questionRepository,
                    final QuestionTopicRepository questionTopicRepository) {
        this.jobRepository = jobRepository;
        this.leetcodeClient = throttledLeetcodeClient;
        this.questionRepository = questionRepository;
        this.questionTopicRepository = questionTopicRepository;
        this.rateLimiter = initializeBucket();
    }

    /**
     * Queries the repo to find incomplete jobs by max size and returns them.
     * 
     * @param maxSize the maximum number of jobs to claim
     * @return list of incomplete jobs to process
     */
    private List<Job> claimBatch(final int maxSize) {
        return jobRepository.findIncompleteJobs(maxSize);
    }

    @Scheduled(initialDelay = 0, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void drainQueue() {
        while (true) {
            List<Job> jobs = claimBatch(MAX_JOBS_PER_RUN);

            if (jobs.isEmpty()) {
                log.info("No more work to do");
                break;
            }

            log.info("Found {} jobs to process", jobs.size());

            for (Job job : jobs) {
                try {
                    waitForToken();
                    fetchAndUpdate(job);
                } catch (Exception e) {
                    log.error("Failed to process job with id: {} for questionId: {}",
                                    job.getId(), job.getQuestionId());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Actually processes the job, makes updates to the repo as necessary to
     * indicate the state that the job is in. This will fetch the question from our
     * backend first, then use the leetcode ID to get submission details.
     */
    private void fetchAndUpdate(final Job job) {
        log.info("Processing job {} for questionId: {}", job.getId(), job.getQuestionId());

        job.setStatus(JobStatus.PROCESSING);
        job.setProcessedAt(StandardizedOffsetDateTime.now());
        boolean success = jobRepository.updateJob(job);
        if (!success) {
            throw new RuntimeException("Failed to update job status to PROCESSING");
        }

        try {
            log.debug("Fetching question from backend with ID: {}", job.getQuestionId());
            Question question = questionRepository.getQuestionById(job.getQuestionId());

            if (question == null) {
                throw new RuntimeException("No question found in backend with ID: " + job.getQuestionId());
            }

            log.debug("Found question: {} ({})", question.getQuestionTitle(), question.getQuestionSlug());

            if (question.getSubmissionId() != null && !question.getSubmissionId().isEmpty()) {
                try {
                    int submissionId = Integer.parseInt(question.getSubmissionId());
                    log.debug("Fetching submission details from Leetcode for submission ID: {}", submissionId);

                    var detailedSubmission = leetcodeClient.findSubmissionDetailBySubmissionId(submissionId);

                    if (detailedSubmission != null) {
                        log.debug("Successfully fetched submission details for submission ID: {}", submissionId);

                        question.setRuntime(detailedSubmission.getRuntimeDisplay());
                        question.setMemory(detailedSubmission.getMemoryDisplay());
                        question.setCode(detailedSubmission.getCode());

                        if (detailedSubmission.getLang() != null) {
                            question.setLanguage(detailedSubmission.getLang().getName());
                        }

                        questionRepository.updateQuestion(question);
                        log.debug("Successfully updated question ID: {} with submission details", question.getId());
                    } else {
                        log.warn("No detailed submission found for submission ID: {}", submissionId);
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid submission ID format '{}' for question {}", question.getSubmissionId(), question.getId());
                }
            }

            job.setStatus(JobStatus.COMPLETE);
            job.setCompletedAt(StandardizedOffsetDateTime.now());
            success = jobRepository.updateJob(job);
            if (!success) {
                throw new RuntimeException("Failed to update job status to COMPLETE");
            }

            log.info("Successfully completed job {} for question: {} ({})",
                            job.getId(), question.getQuestionTitle(), question.getQuestionSlug());

        } catch (Exception e) {
            job.setStatus(JobStatus.INCOMPLETE);
            job.setProcessedAt(null);
            jobRepository.updateJob(job);

            log.error("Failed to process job {} for questionId: {}", job.getId(), job.getQuestionId());
            e.printStackTrace();
            throw new RuntimeException("Job processing failed", e);
        }
    }

}