package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.publish.workflow.util.FileStatLogger;
import java.io.File;
import java.util.List;

public class HollowBlobDeleteFileJob extends PublishWorkflowPublicationJob {
    private final List<PublicationJob> publishJobs;
    protected final String[] filesToDelete;

    public HollowBlobDeleteFileJob(PublishWorkflowContext ctx, List<PublicationJob> copyJobs, long version, String... filesToDelete) {
        super(ctx, "delete-files", version);
        this.publishJobs = copyJobs;
        this.filesToDelete = filesToDelete;
    }

    @Override
    protected boolean executeJob() {
        // Spot to trigger Cycle Monkey if enabled
        ctx.getCycleMonkey().doMonkeyBusiness("HollowBlobDeleteFileJob");

        for(String filename : filesToDelete) {
            File f = new File(filename);
            if(f.exists()) {
                FileStatLogger.logFileState(ctx.getLogger(), TransformerLogTag.DeletedTmpFile, "cleanup File", f);
                f.delete();
            }
        }

        return true;
    }

    @Override
    public boolean isEligible() {
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
