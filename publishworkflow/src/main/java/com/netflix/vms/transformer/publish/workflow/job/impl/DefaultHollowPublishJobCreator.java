package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.netflix.config.FastProperty;
import com.netflix.config.FastProperty.BooleanProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowConfig;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.TransformerPublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.*;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;

public class DefaultHollowPublishJobCreator implements HollowPublishJobCreator {
    private static final BooleanProperty BIG_RED_BUTTON = new FastProperty.BooleanProperty("com.netflix.vms.server.bigredbutton", false);

    /* dependencies */
    private HollowBlobDataProvider hollowBlobDataProvider;
    private final PlaybackMonkeyTester playbackMonkeyTester;
    private final ValidationVideoRanker videoRanker;

    /* fields */
    ///TODO: VIP changes for red/black?
    private final PublishWorkflowContext ctx;

    public DefaultHollowPublishJobCreator(TransformerContext transformerContext,
            HollowBlobDataProvider hollowBlobDataProvider, PlaybackMonkeyTester playbackMonkeyTester,
            ValidationVideoRanker videoRanker, String vip, PublishWorkflowConfig config) {
        this.hollowBlobDataProvider = hollowBlobDataProvider;
        this.playbackMonkeyTester = playbackMonkeyTester;
        this.videoRanker = videoRanker;
        this.ctx = new TransformerPublishWorkflowContext(transformerContext, config,
                new HermesVipAnnouncer(
                        new HermesBlobAnnouncer(transformerContext.platformLibraries().getHermesPublisher(),
                                HermesTopicProvider.HOLLOWBLOB_TOPIC_PREFIX),
                        transformerContext.platformLibraries().getHermesSubscriber(), BIG_RED_BUTTON),
                vip);
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
        return new HollowBlobDeleteFileJob(ctx, copyJobs, version, filesToDelete);
    }

    @Override
    public DelayJob createDelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
        return new HollowBlobDelayJob(ctx, dependency, delayMillis, cycleVersion);
    }

    @Override
    public CircuitBreakerJob createCircuitBreakerJob(String vip, long newVersion, File snapshotFile, File deltaFile, File reverseDeltaFile) {
        return new HollowBlobCircuitBreakerJob(ctx, newVersion, snapshotFile, deltaFile, reverseDeltaFile, hollowBlobDataProvider);
    }

    @Override
    public PoisonStateMarkerJob createPoisonStateMarkerJob(PublicationJob validationJob, long newVersion) {
        return new CassandraPoisonStateMarkerJob(ctx, validationJob, newVersion);
    }

    @Override
    public CanaryRollbackJob createCanaryRollbackJob(String vip, long cycleVersion, long priorVersion,CanaryValidationJob validationJob) {
        return new HermesCanaryRollbackJob(ctx, vip, cycleVersion, priorVersion, validationJob);
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
		return new HermesCanaryAnnounceJob(ctx, vip, newVersion, region, beforeCanaryAnnounceHook, previousCycleValidationJob,
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
        return new HermesAutoPinbackJob(ctx, announcement, waitMillis, cycleVersion);
    }
}
