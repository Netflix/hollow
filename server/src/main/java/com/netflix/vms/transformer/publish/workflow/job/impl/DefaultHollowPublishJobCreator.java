package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.TransformerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowConfig;
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
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;
import com.netflix.vms.transformer.util.VMSCassandraHelper;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DefaultHollowPublishJobCreator implements HollowPublishJobCreator {

    ///TODO: VIP changes for red/black?
    private final PublishWorkflowContext ctx;
	private final HollowBlobDataProvider hollowBlobDataProvider;
	private final PlaybackMonkeyTester playbackMonkeyTester;
	private final ValidationVideoRanker videoRanker;

    public DefaultHollowPublishJobCreator(String vip, PublishWorkflowConfig config, TransformerLogger logger) {
        VMSCassandraHelper validationStatsCassandraHelper = new VMSCassandraHelper("cass_dpt", "hollow_publish_workflow", "hollow_validation_stats");
        VMSCassandraHelper canaryResultsCassandraHelper = new VMSCassandraHelper("cass_dpt", "canary_validation", "canary_results");
		this.hollowBlobDataProvider = new HollowBlobDataProvider();
		this.playbackMonkeyTester = new PlaybackMonkeyTester();
		this.videoRanker = new ValidationVideoRanker(hollowBlobDataProvider);
		this.ctx = new PublishWorkflowContext(vip, logger, config, validationStatsCassandraHelper, canaryResultsCassandraHelper);
    }

    @Override
    public AnnounceJob createAnnounceJob(String vip, long priorVersion, long newVersion, RegionEnum region, CanaryValidationJob validationJob, DelayJob delayJob, AnnounceJob previousAnnounceJob) {
        return new HermesAnnounceJob(ctx, priorVersion, newVersion, region, validationJob, delayJob, previousAnnounceJob);
    }

    @Override
    public HollowBlobPublishJob createPublishJob(String vip, PublishType jobType, long previousVersion, long version, RegionEnum region, File fileToUpload) {
        return new FileStoreHollowBlobPublishJob(ctx, previousVersion, version, jobType, region, fileToUpload);
    }

    @Override
    public HollowBlobDeleteFileJob createDeleteFileJob(List<PublicationJob> copyJobs, long version, String... filesToDelete) {
        return new HollowBlobDeleteFileJob(copyJobs, version, filesToDelete);
    }

    @Override
    public DelayJob createDelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
        return new HollowBlobDelayJob(dependency, delayMillis, cycleVersion);
    }

    @Override
    public CircuitBreakerJob createCircuitBreakerJob(String vip, long newVersion, File snapshotFile, File deltaFile, File reverseDeltaFile) {
        return new HollowBlobCircuitBreakerJob(ctx, newVersion, snapshotFile, deltaFile, reverseDeltaFile, hollowBlobDataProvider);
    }

    @Override
    public PoisonStateMarkerJob createPoisonStateMarkerJob(PublicationJob validationJob, long newVersion) {
        return new CassandraPoisonStateMarkerJob(validationJob, ctx, newVersion);
    }

    @Override
    public CanaryRollbackJob createCanaryRollbackJob(String vip, long cycleVersion, long priorVersion,CanaryValidationJob validationJob) {
        return new HermesCanaryRollbackJob(vip, cycleVersion, priorVersion, validationJob);
    }

    @Override
    public CanaryValidationJob createCanaryValidationJob(String vip, long cycleVersion, Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs,
            Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs) {
        return new CassandraCanaryValidationJob(ctx, cycleVersion, beforeCanaryAnnounceJobs, afterCanaryAnnounceJobs, videoRanker);
    }

	@Override
	public BeforeCanaryAnnounceJob createBeforeCanaryAnnounceJob(String vip,
			long newVersion, RegionEnum region,
			CircuitBreakerJob circuitBreakerJob,
			CanaryValidationJob previousCycleValidationJob,
			List<PublicationJob> newPublishJobs,
			CanaryRollbackJob previousCycleCanaryRoleBackJob) {
		return new HollowBlobBeforeCanaryAnnounceJob(ctx, newVersion, region, circuitBreakerJob, previousCycleValidationJob,
				newPublishJobs, previousCycleCanaryRoleBackJob, playbackMonkeyTester, videoRanker);
	}

	@Override
	public CanaryAnnounceJob createCanaryAnnounceJob(String vip,
			long newVersion, RegionEnum region,
			BeforeCanaryAnnounceJob beforeCanaryAnnounceHook,
			CanaryValidationJob previousCycleValidationJob,
			List<PublicationJob> newPublishJobs) {
		return new HermesCanaryAnnounceJob(vip, newVersion, region, beforeCanaryAnnounceHook, previousCycleValidationJob,
				newPublishJobs);
	}

	@Override
	public AfterCanaryAnnounceJob createAfterCanaryAnnounceJob(String vip,
			long newVersion, RegionEnum region,
			BeforeCanaryAnnounceJob beforeCanaryAnnounceJob,
			CanaryAnnounceJob canaryAnnounceJob) {
		return new HollowBlobAfterCanaryAnnounceJob(ctx, newVersion, region, beforeCanaryAnnounceJob,
				canaryAnnounceJob, playbackMonkeyTester, videoRanker);
	}

    @Override
    public AutoPinbackJob createAutoPinbackJob(AnnounceJob announcement, long waitMillis, long cycleVersion) {
        return new HermesAutoPinbackJob(announcement, waitMillis, cycleVersion);
    }

}
