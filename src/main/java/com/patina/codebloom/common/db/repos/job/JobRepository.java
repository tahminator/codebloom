package com.patina.codebloom.common.db.repos.job;

import java.util.List;

import com.patina.codebloom.common.db.models.job.Job;

public interface JobRepository {
    /**
     * Creates a new job in the database. The id and createdAt properties of the job
     * object will be set by this method.
     * 
     * @param job the job to create
     * @return the created job with id and createdAt set
     */
    Job createJob(Job job);

    /**
     * Finds a job by its ID.
     * 
     * @param id the job ID
     * @return the job if found, null otherwise
     */
    Job findJobById(String id);

    /**
     * Finds incomplete jobs ordered by creation date (oldest first).
     * 
     * @param maxJobs the maximum number of jobs to return
     * @return list of incomplete jobs ordered by oldest first
     */
    List<Job> findIncompleteJobs(int maxJobs);

    /**
     * Updates an existing job in the database.
     * 
     * @param job the job to update
     * @return true if the update was successful, false otherwise
     */
    boolean updateJob(Job job);
}