package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import java.io.File;

/**
 * A no-op version of a {@link HollowBlobPublishJob} used in unit tests.
 */
public class TestHollowBlobPublishJob extends HollowBlobPublishJob {
    public TestHollowBlobPublishJob(PublishWorkflowContext context, String vip, long inputVersion,
            long previousVersion, long version, PublishType jobType, File fileToUpload, boolean noStreams) {
        super(context, vip, inputVersion, previousVersion, version, jobType, fileToUpload, noStreams);
    }

    @Override public boolean executeJob() {
        return true;
    }
}
