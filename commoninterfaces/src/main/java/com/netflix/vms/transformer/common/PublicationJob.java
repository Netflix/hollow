package com.netflix.vms.transformer.common;

public interface PublicationJob extends Runnable {
    public static final long NOT_YET = Long.MIN_VALUE;

    long getActualStartTimestamp();

    long getCompletedTimestamp();

    long getCycleVersion();

    String getDisplayableStatus();

    long getFailedTimestamp();

    long getFinishedTimestamp();

    String getJobName();

    boolean hasJobFailed();

    boolean isComplete();

    boolean isEligible();

    void setJobQueue(PublicationJobQueue publicationJobQueue);
}
