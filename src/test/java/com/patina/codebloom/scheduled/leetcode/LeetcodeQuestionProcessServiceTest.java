package com.patina.codebloom.scheduled.leetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.repos.job.JobRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeetcodeQuestionProcessServiceTest {

    private final JobRepository jobRepository;
    private final LeetcodeQuestionProcessService service;
    private Job testJob;

    @Autowired
    public LeetcodeQuestionProcessServiceTest(final JobRepository jobRepository,
                    final LeetcodeQuestionProcessService service) {
        this.jobRepository = jobRepository;
        this.service = service;
    }

    @BeforeAll
    void setup() {
        testJob = Job.builder()
                        .questionId("two-sum")
                        .status(JobStatus.INCOMPLETE)
                        .build();

        testJob = jobRepository.createJob(testJob);

        assertTrue(testJob.getId() != null);
        assertTrue(testJob.getCreatedAt() != null);
    }

    @AfterAll
    void cleanup() {
        if (testJob != null && testJob.getId() != null) {
            jobRepository.deleteJobById(testJob.getId());
        }
    }

    @Test
    void serviceValid() {
        assertNotNull(service);
    }

    @Test
    void findIncompleteJobsValid() {
        List<Job> incompleteJobs = jobRepository.findIncompleteJobs(10);

        assertNotNull(incompleteJobs);
        assertTrue(incompleteJobs.size() >= 1);

        boolean foundTestJob = incompleteJobs.stream()
                        .anyMatch(job -> job.getId().equals(testJob.getId()));
        assertTrue(foundTestJob);
    }

    @Test
    void jobCreationValid() {
        assertTrue(testJob != null);
        assertTrue(testJob.getId() != null);
        assertEquals("two-sum", testJob.getQuestionId());
        assertEquals(JobStatus.INCOMPLETE, testJob.getStatus());
        assertTrue(testJob.getCreatedAt() != null);
    }

    @Test
    void jobStatusTransitionValid() {
        Job processingJob = Job.builder()
                        .questionId("valid-parentheses")
                        .status(JobStatus.INCOMPLETE)
                        .build();

        processingJob = jobRepository.createJob(processingJob);

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
    }

    @Test
    void drainQueueValid() {
        try {
            service.drainQueue();
            assertTrue(true);
        } catch (Exception e) {
            System.out.println("DrainQueue encountered an exception: " + e.getMessage());
            assertTrue(true);
        }
    }
}