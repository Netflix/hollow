package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.job.DelayJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

import com.netflix.config.FastProperty;

public class HollowBlobDelayJob extends DelayJob {

    private static final FastProperty.BooleanProperty BIG_GREEN_BUTTON = new FastProperty.BooleanProperty("com.netflix.vms.server.biggreenbutton", false);
    private static final long BIG_GREEN_BUTTON_POLL_MILLIS = 10000L;

    public HollowBlobDelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
        super(dependency, delayMillis, cycleVersion);
    }

    @Override
    protected boolean executeJob() {
        long endTime = System.currentTimeMillis() + delayMillis;

        while(System.currentTimeMillis() < endTime) {
            if(BIG_GREEN_BUTTON.get())
                return true;

            long sleepMillis = Math.min(BIG_GREEN_BUTTON_POLL_MILLIS, endTime - System.currentTimeMillis());

            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException ignore) { }
        }

        return true;
    }

}
