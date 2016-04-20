package com.netflix.vms.transformer.publish.workflow.job.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PublicationJobQueue {

    private final List<PublicationJob> waitingJobs;
    private final PublicationHistory history;

    public PublicationJobQueue() {
        this.waitingJobs = new ArrayList<PublicationJob>();
        this.history = new PublicationHistory();
    }

    synchronized void jobDone() {
        this.notifyAll();
    }

    public synchronized boolean offer(PublicationJob job) {
        history.addJob(job);
        waitingJobs.add(job);
        this.notifyAll();
        return true;
    }

    public synchronized PublicationJob take() throws InterruptedException {
        PublicationJob job = poll();

        try {
            while(job == null) {
                this.wait(1800000L);
                job = poll();
            }
        } catch (InterruptedException ignore) { }

        return job;
    }

    private PublicationJob poll() {
        Iterator<PublicationJob> iter = waitingJobs.iterator();
        while(iter.hasNext()) {
            PublicationJob job = iter.next();

            if(job.hasJobFailed()) {
                iter.remove();
            } else if(job.isEligible()) {
                iter.remove();
                return job;
            }
        }

        return null;
    }

    public synchronized void awaitAllJobsCompleted() {
        while(!history.areAllJobsFinished()) {
            try {
                this.wait();
            } catch (InterruptedException e) { }
        }
    }

    public PublicationHistory getHistory() {
        return history;
    }

}
