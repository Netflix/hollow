package com.netflix.vms.transformer.common;

import java.util.List;

public interface PublicationHistory {
    void addJob(PublicationJob job);
    boolean areAllJobsFinished();
    List<PublicationJob> getJobsSortedByCompletedOrFailedTimestamp();
    List<PublicationJob> getJobsSortedByCycleVersion();
    List<PublicationJob> getJobsSortedByStartTimestamp();
}
