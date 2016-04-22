package com.netflix.vms.transformer.publish.workflow.job.framework;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A job goes through three phases
 *
 * 1) Ineligible to run -- the required dependencies have not yet finished.
 * 2) Running -- The job is executing.
 * 3) Completed -- The job is complete.
 *
 */
public abstract class PublicationJob implements Runnable {

    public static final long NOT_YET = Long.MIN_VALUE;

    private PublicationJobQueue queue;

    private long startTimestamp = NOT_YET;
    private long completedTimestamp = NOT_YET;
    private long failedTimestamp = NOT_YET;

    //// These are for logging purposes
    private final String jobName;
    private final long cycleVersion;

    private boolean failedExplicitly;
    private boolean failedWithException;
    private boolean failedBasedOnDependencies;

    /**
     * Create a PublicationJob.
     */
    public PublicationJob(String jobName, long cycleVersion) {
        this.cycleVersion = cycleVersion;
        this.jobName = jobName;
    }

    void setJobQueue(PublicationJobQueue jobQueue) {
        this.queue = jobQueue;
    }

    @Override
    public final void run() {
        notifyBegun();

        try {
            boolean success = executeJob();
            if(!success || isFailedBasedOnDependencies()) {
                failedExplicitly = true;
                notifyFailed();
            } else{
                notifyCompleted();
            }
        } catch(Throwable t) {
            failedWithException = true;
            notifyFailed();
            throw t;
        }
    }

    protected abstract boolean executeJob();

    private final void notifyBegun() {
        startTimestamp = System.currentTimeMillis();
    }

    private final void notifyCompleted() {
        completedTimestamp = System.currentTimeMillis();
        queue.jobDone();
    }

    private final void notifyFailed() {
        failedTimestamp = System.currentTimeMillis();
        queue.jobDone();
    }

    public final boolean isComplete() {
        return completedTimestamp != Long.MIN_VALUE;
    }

    public final long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public final long getFailedTimestamp() {
        return failedTimestamp;
    }

    public String getJobName() {
        return jobName;
    }

    public final long getActualStartTimestamp() {
        return startTimestamp;
    }

    public long getCycleVersion() {
        return cycleVersion;
    }

    public final boolean hasJobFailed() {
        if(failedExplicitly || failedWithException || failedBasedOnDependencies)
            return true;
        else if(isFailedBasedOnDependencies()) {
            failedBasedOnDependencies = true;
            failedTimestamp = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    protected abstract boolean isEligible();
    protected abstract boolean isFailedBasedOnDependencies();

    protected boolean jobExistsAndFailed(PublicationJob job) {
        return job != null && job.hasJobFailed();
    }

    protected boolean jobDoesNotExistOrFailed(PublicationJob job) {
        return job == null || job.hasJobFailed();
    }

    protected boolean jobExistsAndCompletedSuccessfully(PublicationJob job) {
        return job != null && job.isComplete();
    }

    protected boolean jobDoesNotExistOrCompletedSuccessfully(PublicationJob job) {
        return job == null || job.isComplete();
    }

    public long getFinishedTimestamp() {
        if(completedTimestamp == NOT_YET)
            return failedTimestamp;
        return completedTimestamp;
    }


    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
        StringBuilder builder = new StringBuilder();
        builder.append(jobName);
        builder.append(" | ");
        builder.append(cycleVersion);
        builder.append(" | ");
        builder.append(getDisplayableStatus());
        builder.append(" | start:");
        builder.append(startTimestamp == NOT_YET ? "N/A" : dateFormat.format(new Date(startTimestamp)));
        builder.append(" | finish:");
        builder.append(getFinishedTimestamp() == NOT_YET ? "N/A" : dateFormat.format(new Date(getFinishedTimestamp())));

        return builder.toString();
    }

    public String getDisplayableStatus() {
        if(completedTimestamp != NOT_YET)
            return "SUCCESS";
        if(failedTimestamp != NOT_YET)
            return "FAILED";
        if(startTimestamp == NOT_YET)
            return "QUEUED";
        return "EXECUTING";
    }
}
