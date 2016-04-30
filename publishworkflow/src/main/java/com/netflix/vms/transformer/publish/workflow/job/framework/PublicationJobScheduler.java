package com.netflix.vms.transformer.publish.workflow.job.framework;

import java.util.concurrent.ThreadPoolExecutor;

import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.PublicationHistory;
import com.netflix.vms.transformer.common.PublicationJob;

public class PublicationJobScheduler {

    private final PublishWorkflowPublicationJobQueue publicationJobQueue;
    private final ThreadPoolExecutor threadPool;

    public PublicationJobScheduler() {
        publicationJobQueue = new PublishWorkflowPublicationJobQueue();
        threadPool = new SimultaneousExecutor();

        startListening();
    }

    public void submitJob(PublicationJob job) {
        job.setJobQueue(publicationJobQueue);
        publicationJobQueue.offer(job);
    }

    public void startListening() {
        Thread takingThread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        PublicationJob readyJob = publicationJobQueue.take();
                        threadPool.execute(readyJob);
                    } catch (InterruptedException ignore) { }
                }
            }
        });

        takingThread.setDaemon(true);
        takingThread.start();
    }

    public void awaitAllJobsCompleted() {
        publicationJobQueue.awaitAllJobsCompleted();
    }

    public PublicationHistory getHistory() {
        return publicationJobQueue.getHistory();
    }
}
