package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.publish.workflow.util.FileStatLogger;
import java.io.File;
import java.util.List;

/**
 * A job that deletes files from the local filesystem. This is generally used to delete
 * hollow snapshot, delta, and reverse delta files stored on disk, in the java temporary
 * folder.
 */
public class HollowBlobDeleteFileJob extends PublishWorkflowPublicationJob {
    private final List<PublicationJob> circuitBreakerAndPublishJobs;
    private final String[] filesToDelete;

    public HollowBlobDeleteFileJob(PublishWorkflowContext ctx,
            List<PublicationJob> circuitBreakerAndPublishJobs, long version, String... filesToDelete) {
        super(ctx, "delete-files", version);
        this.circuitBreakerAndPublishJobs = circuitBreakerAndPublishJobs;
        this.filesToDelete = filesToDelete;
    }

    @Override public boolean executeJob() {
        // Spot to trigger Cycle Monkey if enabled
        ctx.getCycleMonkey().doMonkeyBusiness("HollowBlobDeleteFileJob");

        for (String filename : filesToDelete) {
            File f = new File(filename);
            if (f.exists()) {
                FileStatLogger.logFileState(ctx.getLogger(), TransformerLogTag.DeletedTmpFile, "cleanup File", f);
                f.delete();
            }
        }
        return true;
    }

    @Override
    public boolean isEligible() {
        for (PublicationJob job : circuitBreakerAndPublishJobs) {
            if (!job.isComplete() && !job.hasJobFailed())
                return false;
        }
        return true;
    }

    @Override
    protected boolean isFailedBasedOnDependencies() {
        return false;
    }
}
