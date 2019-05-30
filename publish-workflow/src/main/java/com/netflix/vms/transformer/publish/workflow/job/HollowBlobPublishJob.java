package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import java.io.File;

public abstract class HollowBlobPublishJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    protected CycleInputs cycleInputs;
    protected final long previousVersion;
    protected final boolean isNostreams;
    protected final File fileToUpload;
    protected final PublishType jobType;

    public HollowBlobPublishJob(PublishWorkflowContext ctx, String vip, CycleInputs cycleInputs,
            long previousVersion, long version, PublishType jobType, File fileToUpload, boolean isNostreams) {
        super(ctx, "publish-"+ jobType.toString(), version);
        this.vip = vip;
        this.cycleInputs = cycleInputs;
        this.previousVersion = previousVersion;
        this.jobType = jobType;
        this.fileToUpload = fileToUpload;
        this.isNostreams = isNostreams;
    }

    public PublishType getPublishType() {
        return jobType;
    }

    @Override
    public boolean isEligible() {
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return false;
    }

    public static enum PublishType {
        DELTA,
        SNAPSHOT,
        REVERSEDELTA
    }
}
