package com.netflix.vms.transformer.publish.workflow.job.framework;

import static com.netflix.vms.transformer.common.publish.workflow.PublicationJob.NOT_YET;

import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PublishWorkflowPublicationHistory implements PublicationHistory {

    private final List<PublicationJob> jobs;

    public PublishWorkflowPublicationHistory() {
        this.jobs = Collections.synchronizedList(new ArrayList<>());
    }

    public void addJob(PublicationJob job) {
        this.jobs.add(job);
    }

    public List<PublicationJob> getJobsSortedByCycleVersion() {
        List<PublicationJob> jobs = new ArrayList<>(this.jobs);
        Collections.sort(jobs, new Comparator<PublicationJob>() {
            public int compare(PublicationJob o1, PublicationJob o2) {
                int cycleVersionSortResult = compareLongs(o1.getCycleVersion(), o2.getCycleVersion());
                if(cycleVersionSortResult != 0)
                    return cycleVersionSortResult;
                return compareLongs(o1.getActualStartTimestamp(), o2.getActualStartTimestamp());
            }
        });
        return jobs;
    }

    public List<PublicationJob> getJobsSortedByStartTimestamp() {
        List<PublicationJob> jobs = new ArrayList<>(this.jobs);
        Collections.sort(jobs, new Comparator<PublicationJob>() {
            public int compare(PublicationJob o1, PublicationJob o2) {
                return compareLongs(o1.getActualStartTimestamp(), o2.getActualStartTimestamp());
            }
        });
        return jobs;
    }

    public List<PublicationJob> getJobsSortedByCompletedOrFailedTimestamp() {
        List<PublicationJob> jobs = new ArrayList<>(this.jobs);
        Collections.sort(jobs, new Comparator<PublicationJob>() {
            public int compare(PublicationJob o1, PublicationJob o2) {
                return compareLongs(getCompletedOrFailedTimestamp(o1), getCompletedOrFailedTimestamp(o2));
            }
        });
        return jobs;
    }

    public boolean areAllJobsFinished() {
        for(PublicationJob job : jobs) {
            if(getCompletedOrFailedTimestamp(job) == Long.MAX_VALUE)
                return false;
        }
        return true;
    }

    private long getCompletedOrFailedTimestamp(PublicationJob job) {
        if(job.getFailedTimestamp() != NOT_YET)
            return job.getFailedTimestamp();
        if(job.getCompletedTimestamp() != NOT_YET)
            return job.getCompletedTimestamp();
        return Long.MAX_VALUE;
    }

    private int compareLongs(long l1, long l2) {
        if(l1 > l2)
            return 1;
        if(l2 > l1)
            return -1;
        return 0;
    }
}
