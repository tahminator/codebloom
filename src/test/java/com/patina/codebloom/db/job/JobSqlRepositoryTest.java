package com.patina.codebloom.db.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import com.patina.codebloom.common.db.repos.job.JobRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class JobSqlRepositoryTest extends BaseRepositoryTest {

    private JobRepository jobRepository;
    private Job testJob;
    private String mockQuestionId = UUID.randomUUID().toString();

    @Autowired
    public JobSqlRepositoryTest(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @BeforeAll
    void setup() {
        testJob = Job.builder()
                .questionId(mockQuestionId)
                .status(JobStatus.INCOMPLETE)
                .nextAttemptAt(StandardizedOffsetDateTime.now().minusHours(1))
                .build();

        jobRepository.createJob(testJob);
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = jobRepository.deleteJobById(testJob.getId());

        if (!isSuccessful) {
            fail("Failed to delete test job");
        }

        // would return false, but it's ok - super simple method - we just call it to
        // make sure it works.
        jobRepository.deleteAllJobs();
    }

    @Test
    @Order(1)
    void testFindJobById() {
        Job foundJob = jobRepository.findJobById(testJob.getId());
        assertNotNull(foundJob);
        assertEquals(testJob, foundJob);
    }

    @Test
    @Order(2)
    void testFindIncompleteJobs() {
        List<Job> incompleteJobs = jobRepository.findIncompleteJobs(10);
        assertNotNull(incompleteJobs);
        assertTrue(incompleteJobs.contains(testJob));
    }

    @Test
    @Order(3)
    void testUpdateJob() {
        testJob.setProcessedAt(StandardizedOffsetDateTime.now());
        testJob.setCompletedAt(StandardizedOffsetDateTime.now().plusMinutes(5));
        testJob.setNextAttemptAt(StandardizedOffsetDateTime.now().plusMinutes(30));
        testJob.setStatus(JobStatus.COMPLETE);

        boolean updateResult = jobRepository.updateJob(testJob);
        assertTrue(updateResult);

        Job updatedJob = jobRepository.findJobById(testJob.getId());
        assertNotNull(updatedJob);
        assertEquals(JobStatus.COMPLETE, updatedJob.getStatus());
        assertNotNull(updatedJob.getProcessedAt());
        assertNotNull(updatedJob.getCompletedAt());
        assertNotNull(updatedJob.getNextAttemptAt());
    }
}
