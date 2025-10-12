
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
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.job.JobRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
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
     * indicate the state that the job is in. This will fetch the data from
     * leetcode, save it to the question in our db, etc.
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
            log.debug("Fetching question data from Leetcode for slug: {}", job.getQuestionId());
            LeetcodeQuestion leetcodeQuestion = leetcodeClient.findQuestionBySlug(job.getQuestionId());

            if (leetcodeQuestion == null) {
                throw new RuntimeException("No question found on Leetcode for slug: " + job.getQuestionId());
            }

            log.debug("Successfully fetched question: {}", leetcodeQuestion.getQuestionTitle());

            String slug = leetcodeQuestion.getTitleSlug();
            var allIncompleteQuestions = questionRepository.getAllIncompleteQuestions();
            var matchingQuestions = allIncompleteQuestions.stream()
                            .filter(q -> slug.equals(q.getQuestionSlug()))
                            .toList();

            if (matchingQuestions.isEmpty()) {
                log.info("No existing questions found with slug: {}", slug);
            } else {
                log.info("Found {} question(s) to update with slug: {}", matchingQuestions.size(), slug);

                for (Question existingQuestion : matchingQuestions) {
                    try {
                        existingQuestion.setQuestionTitle(leetcodeQuestion.getQuestionTitle());
                        existingQuestion.setDescription(leetcodeQuestion.getQuestion());
                        existingQuestion.setAcceptanceRate(leetcodeQuestion.getAcceptanceRate());
                        existingQuestion.setQuestionLink(leetcodeQuestion.getLink());
                        existingQuestion.setQuestionNumber(leetcodeQuestion.getQuestionId());

                        try {
                            QuestionDifficulty difficulty = QuestionDifficulty.valueOf(
                                            leetcodeQuestion.getDifficulty().toUpperCase());
                            existingQuestion.setQuestionDifficulty(difficulty);
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown difficulty '{}' for question {}",
                                            leetcodeQuestion.getDifficulty(), existingQuestion.getId());
                        }

                        questionRepository.updateQuestion(existingQuestion);

                        for (var leetcodeTopicTag : leetcodeQuestion.getTopics()) {
                            try {
                                String topicSlug = leetcodeTopicTag.getSlug();
                                LeetcodeTopicEnum topicEnum = LeetcodeTopicEnum.fromValue(topicSlug);

                                var existingTopic = questionTopicRepository
                                                .findQuestionTopicByQuestionIdAndTopicEnum(existingQuestion.getId(), topicEnum);

                                if (existingTopic == null) {
                                    var newQuestionTopic = QuestionTopic.builder()
                                                    .questionId(existingQuestion.getId())
                                                    .topicSlug(topicSlug)
                                                    .topic(topicEnum)
                                                    .build();

                                    questionTopicRepository.createQuestionTopic(newQuestionTopic);
                                    log.debug("Added topic '{}' to question {}", topicSlug, existingQuestion.getId());
                                }
                            } catch (Exception e) {
                                log.warn("Failed to process topic '{}' for question {}: {}",
                                                leetcodeTopicTag.getSlug(), existingQuestion.getId(), e.getMessage());
                            }
                        }

                        log.debug("Successfully updated question ID: {}", existingQuestion.getId());
                    } catch (Exception e) {
                        log.error("Failed to update question ID: {}", existingQuestion.getId());
                        e.printStackTrace();
                    }
                }
            }

            job.setStatus(JobStatus.COMPLETE);
            job.setCompletedAt(StandardizedOffsetDateTime.now());
            success = jobRepository.updateJob(job);
            if (!success) {
                throw new RuntimeException("Failed to update job status to COMPLETE");
            }

            log.info("Successfully completed job {} for question: {} ({})",
                            job.getId(), leetcodeQuestion.getQuestionTitle(), leetcodeQuestion.getTitleSlug());

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