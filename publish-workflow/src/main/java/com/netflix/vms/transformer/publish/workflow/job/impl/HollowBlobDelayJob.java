package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.FastProperty;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;

public class HollowBlobDelayJob extends DelayJob {

    private static final FastProperty.BooleanProperty BIG_GREEN_BUTTON = new FastProperty.BooleanProperty("vms.bigGreenButton", false);
    private static final long BIG_GREEN_BUTTON_POLL_MILLIS = 10000L;

    public HollowBlobDelayJob(PublishWorkflowContext ctx, PublicationJob jobToDelay, long delayMillis, long cycleVersion) {
        super(ctx, jobToDelay, delayMillis, cycleVersion);
    }

    @Override public boolean executeJob() {
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
