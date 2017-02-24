package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobPublisher;

import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;
import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
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

public class DefaultHollowPublishJobCreator implements HollowPublishJobCreator {
    /* dependencies */
    private HollowBlobDataProvider hollowBlobDataProvider;
    private final PlaybackMonkeyTester playbackMonkeyTester;
    private final ValuableVideoHolder videoRanker;

    /* fields */
    ///TODO: VIP changes for red/black?
    private PublishWorkflowContext ctx;
    private final DataSlicer dataSlicer;

    public DefaultHollowPublishJobCreator(TransformerContext transformerContext,
            FileStore fileStore,
            NetflixS3BlobPublisher blobPublisher,
            NetflixS3BlobPublisher nostreamsBlobPublisher,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            HollowBlobDataProvider hollowBlobDataProvider, 
            PlaybackMonkeyTester playbackMonkeyTester,
            ValuableVideoHolder videoRanker, 
            DataSlicer dataSlicer,
            Supplier<ServerUploadStatus> serverUploadStatus, 
            String vip) {
        this.hollowBlobDataProvider = hollowBlobDataProvider;
        this.playbackMonkeyTester = playbackMonkeyTester;
        this.videoRanker = videoRanker;
        this.dataSlicer = dataSlicer;
        this.ctx = new TransformerPublishWorkflowContext(transformerContext,
                new HermesVipAnnouncer(hermesBlobAnnouncer),
                serverUploadStatus,
                fileStore,
                blobPublisher,
                nostreamsBlobPublisher,
                vip);
    }

    public PublishWorkflowContext beginStagingNewCycle() {
        ctx = ctx.withCurrentLoggerAndConfig();
        return ctx;
    }

    @Override
    public AnnounceJob createAnnounceJob(String vip, long priorVersion, long newVersion, RegionEnum region, CanaryValidationJob validationJob, DelayJob delayJob, AnnounceJob previousAnnounceJob) {
        return new HermesAnnounceJob(ctx, priorVersion, newVersion, region, validationJob, delayJob, previousAnnounceJob);
    }

    @Override
    public HollowBlobPublishJob createPublishJob(String vip, PublishType jobType, long inputVersion, long previousVersion, long version, File fileToUpload, boolean isNostreams) {
        return new S3HollowBlobPublishJob(ctx, inputVersion, previousVersion, version, jobType, fileToUpload, isNostreams);
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
    public CircuitBreakerJob createCircuitBreakerJob(String vip, long newVersion, File snapshotFile, File deltaFile, File reverseDeltaFile, File nostreamsSnapshotFile, File nostreamsDeltaFile, File nostreamsReverseDeltaFile) {
        return new HollowBlobCircuitBreakerJob(ctx, newVersion, snapshotFile, deltaFile, reverseDeltaFile, nostreamsSnapshotFile, nostreamsDeltaFile, nostreamsReverseDeltaFile, hollowBlobDataProvider);
    }

    @Override
    public PoisonStateMarkerJob createPoisonStateMarkerJob(PublicationJob validationJob, long newVersion) {
        return new CassandraPoisonStateMarkerJob(ctx, validationJob, hollowBlobDataProvider, newVersion);
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
			List<PublicationJob> newPublishJobs) {
		return new HollowBlobBeforeCanaryAnnounceJob(ctx, newVersion, region, circuitBreakerJob, 
				newPublishJobs, playbackMonkeyTester, videoRanker);
	}

	@Override
	public CanaryAnnounceJob createCanaryAnnounceJob(String vip, long newVersion, 
	        RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceHook) {
		return new HermesCanaryAnnounceJob(ctx, vip, newVersion, region, beforeCanaryAnnounceHook);
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

    @Override
    public CreateDevSliceJob createDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency, long inputVersion, long cycleVersion) {
        return new CreateHollowDevSliceJob(ctx, dependency, hollowBlobDataProvider, dataSlicer, inputVersion, cycleVersion);
    }
}
