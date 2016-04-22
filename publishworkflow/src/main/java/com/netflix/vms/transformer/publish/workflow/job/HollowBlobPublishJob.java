package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

import java.io.File;
import com.netflix.config.NetflixConfiguration.RegionEnum;

public abstract class HollowBlobPublishJob extends PublicationJob {

    protected final String vip;
    protected final long previousVersion;
    protected final RegionEnum region;
    protected final File fileToUpload;
    protected final PublishType jobType;

    public HollowBlobPublishJob(String vip, long previousVersion, long version, PublishType jobType, RegionEnum region, File fileToUpload) {
        super("publish-"+ region + "-" + jobType.toString(), version);
        this.vip = vip;
        this.previousVersion = previousVersion;
        this.jobType = jobType;
        this.region = region;
        this.fileToUpload = fileToUpload;
    }

    public PublishType getPublishType() {
        return jobType;
    }

    @Override
    protected boolean isEligible() {
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
