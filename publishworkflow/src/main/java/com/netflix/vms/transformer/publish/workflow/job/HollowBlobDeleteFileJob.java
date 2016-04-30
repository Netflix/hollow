package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;

import java.io.File;
import java.util.List;

public class HollowBlobDeleteFileJob extends PublicationJob {
    private final List<PublicationJob> publishJobs;
    protected final String[] filesToDelete;

    public HollowBlobDeleteFileJob(PublishWorkflowContext ctx, List<PublicationJob> copyJobs, long version, String... filesToDelete) {
        super(ctx, "delete-files", version);
        this.publishJobs = copyJobs;
        this.filesToDelete = filesToDelete;
    }

    @Override
    protected boolean executeJob() {
        for(String filename : filesToDelete) {
            File f = new File(filename);
            if(f.exists()) {
                f.delete();
            }
        }

        return true;
    }

    @Override
    protected boolean isEligible() {
        for(PublicationJob job : publishJobs) {
            if(!job.isComplete() && !job.hasJobFailed())
                return false;
        }

        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return false;
    }
}
