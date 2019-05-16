package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import java.io.File;

public abstract class HollowBlobPublishJob extends PublishWorkflowPublicationJob {

    protected final String vip;
    protected final long inputVersion;
    protected final long gk2InputVersion;
    protected final long previousVersion;
    protected final boolean isNostreams;
    protected final File fileToUpload;
    protected final PublishType jobType;

    public HollowBlobPublishJob(PublishWorkflowContext ctx, String vip, long inputVersion, long gk2InputVersion, long previousVersion, long version, PublishType jobType, File fileToUpload, boolean isNostreams) {
        super(ctx, "publish-"+ jobType.toString(), version);
        this.vip = vip;
        this.inputVersion = inputVersion;
        this.gk2InputVersion = gk2InputVersion;
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
