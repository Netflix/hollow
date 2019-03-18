package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobDeleteFileJob;
import java.util.List;
import java.util.function.Supplier;

/**
 * A version of a {@link HollowBlobDeleteFileJob} used in unit tests. It does nothing by
 * itself, but allows for customizing its executeJob block.
 */
public class TestHollowBlobDeleteFileJob extends HollowBlobDeleteFileJob {
    private final Supplier<Boolean> execute;

    public TestHollowBlobDeleteFileJob(PublishWorkflowContext ctx, Supplier<Boolean> execute,
            List<PublicationJob> copyJobs, long version, String... filesToDelete) {
        super(ctx, copyJobs, version, filesToDelete);
        this.execute = execute;
    }

    @Override public boolean executeJob() {
        return execute.get();
    }
}
