package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.input.CycleInputs;
import com.netflix.vms.transformer.input.datasets.slicers.SlicerFactory;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.TransformerPublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AutoPinbackJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobDeleteFileJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;

public class DefaultHollowPublishJobCreator {
    /* dependencies */
    private HollowBlobDataProvider hollowBlobDataProvider;
    private final PlaybackMonkeyTester playbackMonkeyTester;
    private final ValuableVideoHolder videoRanker;

    /* fields */
    ///TODO: VIP changes for red/black?
    private PublishWorkflowContext ctx;
    private final SlicerFactory slicerFactory;

    public DefaultHollowPublishJobCreator(TransformerContext transformerContext,
            FileStore fileStore,
            Publisher publisher, Publisher nostreamsPublisher,
            Announcer announcer, Announcer nostreamsAnnouncer,
            Announcer canaryAnnouncer,
            Publisher devSlicePublisher, Announcer devSliceAnnouncer,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            HollowBlobDataProvider hollowBlobDataProvider, 
            PlaybackMonkeyTester playbackMonkeyTester,
            ValuableVideoHolder videoRanker,
            SlicerFactory slicerFactory,
            Supplier<ServerUploadStatus> serverUploadStatus,
            String vip) {
        this.hollowBlobDataProvider = hollowBlobDataProvider;
        this.playbackMonkeyTester = playbackMonkeyTester;
        this.videoRanker = videoRanker;
        this.slicerFactory = slicerFactory;
        this.ctx = new TransformerPublishWorkflowContext(transformerContext,
                new HermesVipAnnouncer(hermesBlobAnnouncer),
                serverUploadStatus,
                fileStore,
                publisher, nostreamsPublisher,
                announcer, nostreamsAnnouncer,
                canaryAnnouncer,
                devSlicePublisher, devSliceAnnouncer,
                vip);
    }

    public PublishWorkflowContext getContext() {
        return ctx;
    }

    public PublishWorkflowContext beginStagingNewCycle() {
        ctx = ctx.withCurrentLoggerAndConfig();
        return ctx;
    }

    public AnnounceJob createAnnounceJob(String vip, long priorVersion, long newVersion, RegionEnum region,
            CanaryValidationJob validationJob, DelayJob delayJob, AnnounceJob previousAnnounceJob,
            Map<String, String> metadata) {
        return new HermesAnnounceJob(ctx, priorVersion, newVersion, region, metadata, validationJob, delayJob,
                previousAnnounceJob);
    }


    public HollowBlobPublishJob createPublishJob(String vip, PublishType jobType, boolean isNostreams,
            CycleInputs cycleInputs, long previousVersion, long version,
            File fileToUpload) {
        return new FileStoreHollowBlobPublishJob(ctx, vip, cycleInputs,
                previousVersion, version, jobType, fileToUpload, isNostreams);
    }

    public HollowBlobDeleteFileJob createDeleteFileJob(List<PublicationJob> circuitBreakerAndPublishJobs,
            long version, String... filesToDelete) {
        return new HollowBlobDeleteFileJob(ctx, circuitBreakerAndPublishJobs, version, filesToDelete);
    }

    public DelayJob createDelayJob(PublicationJob dependency, long delayMillis, long cycleVersion) {
        return new HollowBlobDelayJob(ctx, dependency, delayMillis, cycleVersion);
    }

    public CircuitBreakerJob createCircuitBreakerJob(String vip, long newVersion,
            File snapshotFile, File deltaFile, File reverseDeltaFile,
            File nostreamsSnapshotFile, File nostreamsDeltaFile, File nostreamsReverseDeltaFile) {
        return new HollowBlobCircuitBreakerJob(ctx, newVersion,
                snapshotFile, deltaFile, reverseDeltaFile,
                nostreamsSnapshotFile, nostreamsDeltaFile, nostreamsReverseDeltaFile,
                hollowBlobDataProvider);
    }

    public PoisonStateMarkerJob createPoisonStateMarkerJob(PublicationJob validationJob, long newVersion) {
        return new CassandraPoisonStateMarkerJob(ctx, validationJob, hollowBlobDataProvider, newVersion);
    }

    public CanaryRollbackJob createCanaryRollbackJob(String vip, long cycleVersion, long priorVersion,
            CanaryValidationJob validationJob) {
        return new HermesCanaryRollbackJob(ctx, vip, cycleVersion, priorVersion, validationJob);
    }

    public CanaryValidationJob createCanaryValidationJob(String vip, long cycleVersion,
            BeforeCanaryAnnounceJob beforeCanaryAnnounceJobs, AfterCanaryAnnounceJob afterCanaryAnnounceJobs) {
        return new CassandraCanaryValidationJob(ctx, cycleVersion,
                beforeCanaryAnnounceJobs,
                afterCanaryAnnounceJobs,
                hollowBlobDataProvider,
                videoRanker);
    }

	public BeforeCanaryAnnounceJob createBeforeCanaryAnnounceJob(String vip,
			long newVersion,
			CircuitBreakerJob circuitBreakerJob,
			List<PublicationJob> newPublishJobs) {
		return new HollowBlobBeforeCanaryAnnounceJob(ctx, vip, newVersion,
                circuitBreakerJob,
				newPublishJobs,
                playbackMonkeyTester,
                hollowBlobDataProvider,
                videoRanker);
	}

	public CanaryAnnounceJob createCanaryAnnounceJob(String vip, long newVersion,
            BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
		return new HermesCanaryAnnounceJob(ctx, vip, newVersion, beforeCanaryAnnounceHook);
	}

	public AfterCanaryAnnounceJob createAfterCanaryAnnounceJob(String vip, long newVersion,
            CanaryAnnounceJob canaryAnnounceJob) {
		return new HollowBlobAfterCanaryAnnounceJob(ctx, vip, newVersion,
                canaryAnnounceJob,
                playbackMonkeyTester,
                hollowBlobDataProvider,
                videoRanker);
	}

    public AutoPinbackJob createAutoPinbackJob(AnnounceJob announcement, long waitMillis, long cycleVersion) {
        return new HermesAutoPinbackJob(ctx, announcement, waitMillis, cycleVersion);
    }

    public CreateDevSliceJob createDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency,
            CycleInputs cycleInputs, long cycleVersion) {
        return new CreateHollowDevSliceJob(ctx, dependency, hollowBlobDataProvider, slicerFactory,
                cycleInputs, cycleVersion);
    }
}
