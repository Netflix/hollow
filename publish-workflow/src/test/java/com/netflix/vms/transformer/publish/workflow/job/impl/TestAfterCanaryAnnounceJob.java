package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import java.util.Collections;
import java.util.Map;

/**
 * A no-op version of an {@link AfterCanaryAnnounceJob} used in unit tests.
 */
public class TestAfterCanaryAnnounceJob extends AfterCanaryAnnounceJob {
    public TestAfterCanaryAnnounceJob(PublishWorkflowContext context, String vip, long newVersion,
        CanaryAnnounceJob canaryAnnounceJob) {
        super(context, vip, newVersion, canaryAnnounceJob);
    }

    @Override public boolean executeJob() {
        return true;
    }

    @Override
    public Map<VideoCountryKey, Boolean> getTestResults() {
        return Collections.emptyMap();
    }

    @Override
    public void clearResults() {
    }
}
