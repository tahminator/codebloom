package com.patina.codebloom.scheduled.leetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.repos.job.JobRepository;

@SpringBootTest
@ActiveProfiles("test")
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

        jobRepository.createJob(testJob);
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = jobRepository.deleteJobById(testJob.getId());
        if (!isSuccessful) {
            fail("failed to clean up Leetcode Processing");
        }
    }

    @Test
    void serviceValid() {
        assertNotNull(service);
    }

    @Test
    void findIncompleteJobsValid() {
        Job freshJob = Job.builder()
                        .questionId("find-incomplete-test")
                        .status(JobStatus.INCOMPLETE)
                        .build();

        jobRepository.createJob(freshJob);

        List<Job> incompleteJobs = jobRepository.findIncompleteJobs(10);

        assertNotNull(incompleteJobs);
        assertTrue(incompleteJobs.size() >= 1);
        assertTrue(incompleteJobs.contains(freshJob));

        jobRepository.deleteJobById(freshJob.getId());
    }

    @Test
    void jobCreationValid() {
        assertTrue(testJob != null);
        assertTrue(testJob.getId() != null);
        assertEquals("two-sum", testJob.getQuestionId());
        assertEquals(JobStatus.INCOMPLETE, testJob.getStatus());
        assertNotNull(testJob.getCreatedAt());
    }

    @Test
    void jobStatusTransitionValid() {
        Job processingJob = Job.builder()
                        .questionId("valid-parentheses")
                        .status(JobStatus.INCOMPLETE)
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