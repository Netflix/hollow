package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AutoPinbackJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobDeleteFileJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import java.io.File;
import java.util.List;
import java.util.function.Supplier;

/**
 * A version of {@link DefaultHollowPublishJobCreator} used in unit tests. It switches out
 * implementations of all PublicationJobs with test versions, and allows for customizing
 * the "executeJob" methods on a per unit test basis.
 */
public class TestDefaultHollowPublishJobCreator extends DefaultHollowPublishJobCreator {
    private final PublishWorkflowContext context;

	private Supplier<Boolean> circuitBreakerJobExecute = () -> true;
	private Supplier<Boolean> deleteJobExecute = () -> true;

	public TestDefaultHollowPublishJobCreator(PublishWorkflowContext context,
            TransformerContext transformerContext, String vip) {
		super(transformerContext, null,
				null, null,
				null, null,
				null,
				null, null,
				null, null,
				null, null, null,
				null, vip);
		this.context = context;
	}

    @Override
    public PublishWorkflowContext beginStagingNewCycle() {
        return context;
    }

	@Override
	public AnnounceJob createAnnounceJob(String vip, long priorVersion, long newVersion, RegionEnum region,
            CanaryValidationJob validationJob, DelayJob delayJob, AnnounceJob previousAnnounceJob) {
		return new TestAnnounceJob(context, vip, priorVersion, newVersion, region, validationJob,
                delayJob, previousAnnounceJob);
	}

	@Override
	public HollowBlobPublishJob createPublishJob(String vip, PublishType jobType, boolean noStreams,
            long inputVersion, long previousVersion, long version, File fileToUpload) {
		return new TestHollowBlobPublishJob(context, vip, inputVersion, previousVersion, version, jobType, fileToUpload,
				noStreams);
	}

    @Override
    public HollowBlobDeleteFileJob createDeleteFileJob(List<PublicationJob> copyJobs, long version,
			String... filesToDelete) {
        return new TestHollowBlobDeleteFileJob(context, deleteJobExecute, copyJobs, version, filesToDelete);
    }

	@Override
	public DelayJob createDelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
		return new TestDelayJob(context, dependency, delayMillis, cycleVersion);
	}

	@Override
	public CircuitBreakerJob createCircuitBreakerJob(String vip, long newVersion, File snapshot,
            File delta, File reverseDelta, File nostreamsSnapshot, File nostreamsDelta, File nostreamsReverseDelta) {
		return new TestCircuitBreakerJob(context, circuitBreakerJobExecute, vip, newVersion, snapshot,
                delta, reverseDelta, nostreamsSnapshot, nostreamsDelta, nostreamsReverseDelta);
	}

	@Override
	public PoisonStateMarkerJob createPoisonStateMarkerJob(PublicationJob validationJob, long newVersion) {
		return new TestPoisonStateMarkerJob(context, validationJob, newVersion);
	}

	@Override
	public CanaryRollbackJob createCanaryRollbackJob(String vip, long cycleVersion, long priorVersion,
			CanaryValidationJob validationJob) {
		return new TestCanaryRollbackJob(context, vip, cycleVersion, priorVersion, validationJob);
	}

	@Override
	public BeforeCanaryAnnounceJob createBeforeCanaryAnnounceJob(String vip, long newVersion,
            CircuitBreakerJob circuitBreakerJob, List<PublicationJob> newPublishJobs) {
		return new TestBeforeCanaryAnnounceJob(context, vip, newVersion, circuitBreakerJob, newPublishJobs);
	}

	@Override
	public CanaryAnnounceJob createCanaryAnnounceJob(String vip, long newVersion,
			BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
        return new TestCanaryAnnounceJob(context, vip, newVersion, beforeCanaryAnnounceHook);
	}

	@Override
	public AfterCanaryAnnounceJob createAfterCanaryAnnounceJob(String vip, long newVersion,
			CanaryAnnounceJob canaryAnnounceJob) {
		return new TestAfterCanaryAnnounceJob(context, vip, newVersion, canaryAnnounceJob);
	}

	@Override
	public CanaryValidationJob createCanaryValidationJob(String vip,
			long cycleVersion, BeforeCanaryAnnounceJob beforeCanaryAnnounceJobs,
			AfterCanaryAnnounceJob afterCanaryAnnounceJobs) {
		return new TestCanaryValidationJob(context, vip, cycleVersion, afterCanaryAnnounceJobs);
	}

	@Override
	public AutoPinbackJob createAutoPinbackJob(AnnounceJob announcement, long waitMillis, long cycleVersion) {
		return new TestAutoPinbackJob(context, announcement, waitMillis, cycleVersion);
	}

	public void setCircuitBreakerJobExecute(Supplier<Boolean> circuitBreakerJobExecute) {
        this.circuitBreakerJobExecute = circuitBreakerJobExecute;
	}

	public void setDeleteJobExecute(Supplier<Boolean> deleteJobExecute) {
		this.deleteJobExecute = deleteJobExecute;
	}
}
