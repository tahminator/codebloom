package com.patina.codebloom.common.db.repos.job;

import java.util.List;

import com.patina.codebloom.common.db.models.job.Job;

public interface JobRepository {
    /**
     * Creates a new job in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param job - required fields:
     * <ul>
     * <li>questionId</li>
     * <li>status</li>
     * </ul>
     * optional fields:
     * <li>nextAttemptAt</li>
     * <ul>
     * </ul>
     */
    void createJob(Job job);

    /**
     * Finds a job by its ID.
     * 
     * @param id the job ID
     * @return the job if found, null otherwise
     */
    Job findJobById(String id);

    /**
     * Finds incomplete jobs ordered by nextAttemptAt (earliest first).
     * 
     * @param maxJobs the maximum number of jobs to return
     * @return list of incomplete jobs ordered by earliest nextAttemptAt first
     */
    List<Job> findIncompleteJobs(int maxJobs);

    /**
     * Updates an existing job in the database.
     * 
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param job - required fields:
     * <ul>
     * <li>id</li>
     * </ul>
     * updatable fields:
     * <ul>
     * <li>processedAt</li>
     * <li>completedAt</li>
     * <li>nextAttemptAt</li>
     * <li>status</li>
     * </ul>
     * @return true if the update was successful, false otherwise
     */
    boolean updateJob(Job job);

    /**
     * Deletes a job by its ID.
     * 
     * @param id the job ID to delete
     * @return true if the job was deleted successfully, false otherwise
     */
    boolean deleteJobById(String id);

    /**
     * Delete all jobs. <b>BE CAREFUL.</b>
     *
     * @return boolean indicating if all jobs were deleted successfully or not.
     */
    boolean deleteAllJobs();
}
