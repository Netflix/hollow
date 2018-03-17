package com.netflix.vms.transformer.common.publish.workflow;

public interface PublicationJob extends Runnable {
    long NOT_YET = Long.MIN_VALUE;

    long getActualStartTimestamp();

    /**
     * If a job succeeded, returns the success timestamp, otherwise {@link NOT_YET}.
     */
    long getCompletedTimestamp();

    long getCycleVersion();

    String getDisplayableStatus();

    /**
     * If a job failed, returns the fail timestamp, otherwise {@link NOT_YET}.
     */
    long getFailedTimestamp();

    /**
     * If a job failed or succeeded, returns the timestamp it failed or succeeded, otherwise {@link NOT_YET}.
     */
    long getFinishedTimestamp();

    String getJobName();

    /**
     * Returns true if the job has failed. Note that this returns false if the job succeeded.
     */
    boolean hasJobFailed();

    /**
     * Returns true if the job has successfully completed. Note that this returns false if the job failed.
     */
    boolean isComplete();

    /**
     * Returns true if the job is eligible for execution.
     */
    boolean isEligible();

    void setJobQueue(PublicationJobQueue publicationJobQueue);
}
