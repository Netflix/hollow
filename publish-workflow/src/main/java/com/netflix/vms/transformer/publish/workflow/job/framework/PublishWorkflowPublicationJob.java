package com.netflix.vms.transformer.publish.workflow.job.framework;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PublishWorkflowFailed;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJobQueue;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
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
public abstract class PublishWorkflowPublicationJob implements PublicationJob {

    /* dependencies */
    protected final PublishWorkflowContext ctx;

    /* fields */
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
    public PublishWorkflowPublicationJob(PublishWorkflowContext ctx, String jobName, long cycleVersion) {
        this.ctx = ctx;
        this.cycleVersion = cycleVersion;
        this.jobName = jobName;
    }

    public void setJobQueue(PublicationJobQueue jobQueue) {
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
        } catch (Throwable t) {
            failedWithException = true;
            notifyFailed();
            ctx.getLogger().error(PublishWorkflowFailed, "Failure processing job {}", this, t);
            throw t;
        }
    }

    public abstract boolean executeJob();

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

    @Override
    public final boolean isComplete() {
        return completedTimestamp != Long.MIN_VALUE;
    }

    @Override
    public final long getCompletedTimestamp() {
        return completedTimestamp;
    }

    @Override
    public final long getFailedTimestamp() {
        return failedTimestamp;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public final long getActualStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public long getCycleVersion() {
        return cycleVersion;
    }

    @Override
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

    @Override
    public long getFinishedTimestamp() {
        if(completedTimestamp == NOT_YET)
            return failedTimestamp;
        return completedTimestamp;
    }

    @Override
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

    @Override
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
