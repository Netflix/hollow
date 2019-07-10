package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementFailure;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.AnnouncementSuccess;

import com.netflix.aws.file.FileStore;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.cinder.producer.NFHollowAnnouncer;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.input.CycleInputs;
import com.netflix.vms.transformer.input.datasets.slicers.SlicerFactory;
import com.netflix.vms.transformer.publish.status.CycleStatusFuture;
import com.netflix.vms.transformer.publish.status.WorkflowCycleStatusFuture;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AutoPinbackJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import com.netflix.vms.transformer.publish.workflow.job.DelayJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJobScheduler;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesBlobAnnouncer;
import com.netflix.vms.transformer.publish.workflow.job.impl.ValuableVideoHolder;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;
import com.netflix.vms.transformer.publish.workflow.util.VipNameUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;

public class HollowPublishWorkflowStager implements PublishWorkflowStager {

    /* dependencies */
    private final TransformerContext ctx;
    private final PublicationJobScheduler scheduler;
    private final HollowBlobFileNamer fileNamer;
    private PublishRegionProvider regionProvider;
    private final DefaultHollowPublishJobCreator jobCreator;
    private HollowBlobDataProvider circuitBreakerDataProvider;

    /* fields */
    private final String vip;
    private final Map<RegionEnum, AnnounceJob> priorAnnouncedJobs;

    public HollowPublishWorkflowStager(TransformerContext ctx, FileStore fileStore,
            Publisher publisher, Publisher nostreamsPublisher,
            Announcer announcer, Announcer nostreamsAnnouncer,
            Announcer canaryAnnouncer,
            Publisher devSlicePublisher, Announcer devSliceAnnouncer,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            SlicerFactory slicerFactory, Supplier<ServerUploadStatus> uploadStatus, String vip) {
        this(ctx, fileStore,
                publisher, nostreamsPublisher,
                announcer, nostreamsAnnouncer,
                canaryAnnouncer,
                devSlicePublisher, devSliceAnnouncer,
                hermesBlobAnnouncer,
                new HollowBlobDataProvider(ctx), slicerFactory, uploadStatus, vip);
    }

    private HollowPublishWorkflowStager(TransformerContext ctx, FileStore fileStore,
            Publisher publisher, Publisher nostreamsPublisher,
            Announcer announcer, Announcer nostreamsAnnouncer,
            Announcer canaryAnnouncer,
            Publisher devSlicePublisher, Announcer devSliceAnnouncer,
            HermesBlobAnnouncer hermesBlobAnnouncer,
            HollowBlobDataProvider circuitBreakerDataProvider,
            SlicerFactory slicerFactory, Supplier<ServerUploadStatus> uploadStatus, String vip) {
        this(ctx,
                new DefaultHollowPublishJobCreator(ctx, fileStore,
                        publisher, nostreamsPublisher,
                        announcer, nostreamsAnnouncer,
                        canaryAnnouncer,
                        devSlicePublisher, devSliceAnnouncer,
                        hermesBlobAnnouncer,
                        circuitBreakerDataProvider,
                        new PlaybackMonkeyTester(),
                        new ValuableVideoHolder(),
                        slicerFactory, uploadStatus, vip),
                vip);
        this.circuitBreakerDataProvider = circuitBreakerDataProvider;
    }

    HollowPublishWorkflowStager(TransformerContext ctx, DefaultHollowPublishJobCreator jobCreator, String vip) {
        this.ctx = ctx;
        this.scheduler = new PublicationJobScheduler();
        this.fileNamer = new HollowBlobFileNamer(vip);
        this.vip = vip;
        this.regionProvider = new PublishRegionProvider(ctx.getLogger());
        this.priorAnnouncedJobs = new HashMap<>();
        this.jobCreator = jobCreator;

        exposePublicationHistory();
    }

    @Override
    public PublishWorkflowContext getContext() {
        return jobCreator.getContext();
    }

    @Override
    public void notifyRestoredStateEngine(HollowReadStateEngine restoredState, HollowReadStateEngine nostreamsRestoredState) {
        if(circuitBreakerDataProvider != null)
            circuitBreakerDataProvider.notifyRestoredStateEngine(restoredState, nostreamsRestoredState);
    }

    @Override
    public HollowReadStateEngine getCurrentReadStateEngine() {
        if(circuitBreakerDataProvider != null)
            return circuitBreakerDataProvider.getStateEngine();
        throw new IllegalStateException("No HollowReadStateEngine is available");
    }

    @Override
    public CycleStatusFuture triggerPublish(CycleInputs cycleInputs, long previousVersion, long newVersion, Map<String, String> metadata) {
        PublishWorkflowContext ctx = jobCreator.beginStagingNewCycle();

        // Add validation job
        final CircuitBreakerJob circuitBreakerJob = addCircuitBreakerJob(previousVersion, newVersion);

        // Add publish jobs
        final List<PublicationJob> publishJobs = addPublishJobs(cycleInputs, previousVersion, newVersion);

        // / add canary announcement and validation jobs
        final CanaryValidationJob canaryValidationJob = addCanaryJobs(previousVersion, newVersion, circuitBreakerJob, publishJobs);

        final AnnounceJob primaryRegionAnnounceJob = createAnnounceJobForRegion(regionProvider.getPrimaryRegion(),
                previousVersion, newVersion, canaryValidationJob, null, metadata);

        // / add secondary regions
        for (final RegionEnum region : regionProvider.getNonPrimaryRegions()) {
            createAnnounceJobForRegion(region, previousVersion, newVersion, canaryValidationJob, primaryRegionAnnounceJob, metadata);
        }

        addDeleteJob(previousVersion, newVersion, circuitBreakerJob, publishJobs);

        if(ctx.getConfig().isCreateDevSlicedBlob())
            scheduler.submitJob(jobCreator.createDevSliceJob(ctx, primaryRegionAnnounceJob, cycleInputs, newVersion));

        return new WorkflowCycleStatusFuture(ctx.getStatusIndicator(), newVersion);
    }

    private AnnounceJob createAnnounceJobForRegion(RegionEnum region, long previousVerion, long newVersion,
            CanaryValidationJob validationJob, AnnounceJob primaryRegionAnnounceJob, Map<String, String> metadata) {
        DelayJob delayJob = jobCreator.createDelayJob(primaryRegionAnnounceJob,
                regionProvider.getPublishDelayInSeconds(region) * 1000, newVersion);
        scheduler.submitJob(delayJob);

        AnnounceJob announceJob = jobCreator.createAnnounceJob(vip, previousVerion, newVersion, region,
                validationJob, delayJob, priorAnnouncedJobs.get(region), metadata);
        scheduler.submitJob(announceJob);

        priorAnnouncedJobs.put(region, announceJob);

        if (primaryRegionAnnounceJob == null) {
            // / this is the primary region, create an auto pinback job
            AutoPinbackJob autoPinbackJob = jobCreator.createAutoPinbackJob(announceJob, 300000L, newVersion);
            scheduler.submitJob(autoPinbackJob);
        }

        return announceJob;
    }

    /**
     * We run canary only in us-east.
     * BeforeCanaryAnnounceJob only needs to run once
     * CanaryAnnounceJob makes announcement for canary data in all regions (both old and new style)
     * AfterCanaryAnnounceJob only needs to run once
     *
     * CanaryValidationJob depends on BeforeCanaryAnnounceJob and AfterCanaryAnnounceJob
     * PoisonStateMarkerJob needs to run once (will be removed soon)
     * CanaryRollbackJob rollbacks the announcement in all regions (both old and new style)
     */
    private CanaryValidationJob addCanaryJobs(long previousVersion, long newVersion,
            CircuitBreakerJob circuitBreakerJob, List<PublicationJob> publishJobs) {

        BeforeCanaryAnnounceJob beforeCanaryAnnounceJob = jobCreator.createBeforeCanaryAnnounceJob(vip, newVersion,
                circuitBreakerJob, publishJobs);
        scheduler.submitJob(beforeCanaryAnnounceJob);

        CanaryAnnounceJob canaryAnnounceJob = jobCreator.createCanaryAnnounceJob(vip, newVersion,
                beforeCanaryAnnounceJob);
        scheduler.submitJob(canaryAnnounceJob);

        AfterCanaryAnnounceJob afterCanaryAnnounceJob = jobCreator.createAfterCanaryAnnounceJob(vip, newVersion,
                canaryAnnounceJob);
        scheduler.submitJob(afterCanaryAnnounceJob);

        CanaryValidationJob validationJob = jobCreator.createCanaryValidationJob(vip, newVersion,
                beforeCanaryAnnounceJob, afterCanaryAnnounceJob);
        PoisonStateMarkerJob canaryPoisonStateMarkerJob = jobCreator.createPoisonStateMarkerJob(validationJob, newVersion);
        CanaryRollbackJob canaryRollbackJob = jobCreator.createCanaryRollbackJob(vip, newVersion, previousVersion,
                validationJob);

        scheduler.submitJob(validationJob);
        scheduler.submitJob(canaryPoisonStateMarkerJob);
        scheduler.submitJob(canaryRollbackJob);

        return validationJob;
    }

    private CircuitBreakerJob addCircuitBreakerJob(long previousVersion, long newVersion) {
        File snapshotFile = new File(fileNamer.getSnapshotFileName(newVersion));
        File deltaFile = new File(fileNamer.getDeltaFileName(previousVersion, newVersion));
        File reverseDeltaFile = new File(fileNamer.getReverseDeltaFileName(newVersion, previousVersion));

        File nostreamsSnapshotFile = new File(fileNamer.getNostreamsSnapshotFileName(newVersion));
        File nostreamsDeltaFile = new File(fileNamer.getNostreamsDeltaFileName(previousVersion, newVersion));
        File nostreamsReverseDeltaFile = new File(fileNamer.getNostreamsReverseDeltaFileName(newVersion, previousVersion));

        CircuitBreakerJob validationJob = jobCreator.createCircuitBreakerJob(vip, newVersion,
                snapshotFile, deltaFile, reverseDeltaFile,
                nostreamsSnapshotFile, nostreamsDeltaFile, nostreamsReverseDeltaFile);
        scheduler.submitJob(validationJob);

        PoisonStateMarkerJob poisonMarkerJob = jobCreator.createPoisonStateMarkerJob(validationJob, newVersion);
        scheduler.submitJob(poisonMarkerJob);

        return validationJob;
    }

    private void addDeleteJob(long previousVersion, long nextVersion, CircuitBreakerJob circuitBreakerJob,
            List<PublicationJob> publishJobsForCycle) {
        List<PublicationJob> circuitBreakerAndPublishJobs = new ArrayList<>(publishJobsForCycle);
        circuitBreakerAndPublishJobs.add(circuitBreakerJob);
        scheduler.submitJob(jobCreator.createDeleteFileJob(circuitBreakerAndPublishJobs, nextVersion,
                fileNamer.getDeltaFileName(previousVersion, nextVersion),
                fileNamer.getReverseDeltaFileName(nextVersion, previousVersion),
                fileNamer.getSnapshotFileName(nextVersion),
                fileNamer.getNostreamsDeltaFileName(previousVersion, nextVersion),
                fileNamer.getNostreamsReverseDeltaFileName(nextVersion, previousVersion),
                fileNamer.getNostreamsSnapshotFileName(nextVersion)));
    }

    private List<PublicationJob> addPublishJobs(CycleInputs cycleInputs, long previousVersion, long newVersion) {
        File snapshotFile = new File(fileNamer.getSnapshotFileName(newVersion));
        File reverseDeltaFile = new File(fileNamer.getReverseDeltaFileName(newVersion, previousVersion));
        File deltaFile = new File(fileNamer.getDeltaFileName(previousVersion, newVersion));
        File nostreamsSnapshotFile = new File(fileNamer.getNostreamsSnapshotFileName(newVersion));
        File nostreamsReverseDeltaFile = new File(fileNamer.getNostreamsReverseDeltaFileName(newVersion, previousVersion));
        File nostreamsDeltaFile = new File(fileNamer.getNostreamsDeltaFileName(previousVersion, newVersion));

        List<PublicationJob> submittedJobs = new ArrayList<>();
        if (snapshotFile.exists()) {
            HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.SNAPSHOT, false,
                    cycleInputs, previousVersion, newVersion, snapshotFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);

            publishJob = jobCreator.createPublishJob(VipNameUtil.getNoStreamsVip(vip), PublishType.SNAPSHOT, true,
                    cycleInputs, previousVersion, newVersion, nostreamsSnapshotFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        if (deltaFile.exists()) {
            HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.DELTA, false,
                    cycleInputs, previousVersion, newVersion, deltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);

            publishJob = jobCreator.createPublishJob(VipNameUtil.getNoStreamsVip(vip), PublishType.DELTA, true,
                    cycleInputs, previousVersion, newVersion, nostreamsDeltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        if (reverseDeltaFile.exists()) {
            HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.REVERSEDELTA, false,
                    cycleInputs, previousVersion, newVersion, reverseDeltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);

            publishJob = jobCreator.createPublishJob(VipNameUtil.getNoStreamsVip(vip), PublishType.REVERSEDELTA, true,
                    cycleInputs, previousVersion, newVersion, nostreamsReverseDeltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        return submittedJobs;

    }

    // TODO: use constructor injection
    void injectPublishRegionProvider(PublishRegionProvider regionProvider) {
        this.regionProvider = regionProvider;
    }

    private void exposePublicationHistory() {
        ctx.getPublicationHistoryConsumer().accept(scheduler.getHistory());
    }

    @Override
    public void initProducer(
            Supplier<CycleInputs> cycleInputs,
            CinderProducerBuilder pb,
            String vip,
            LongSupplier previousVersion,
            LongSupplier noStreamsPreviousVersion, LongSupplier noStreamsVersion) {
        // Add circuit breaker validator before canary validator
        ValidatorForCircuitBreakers vcbs = new ValidatorForCircuitBreakers(ctx, vip);
        pb.withListener(vcbs);

        pb.withListener(new ValidatorForCanaryPBM(vcbs, this, jobCreator,
                vip, previousVersion));

        pb.withListener(new HermesPublishListener(cycleInputs, jobCreator, vip, previousVersion));
        pb.withListener(new PublishCycleListener(jobCreator, vip));

        // Use special announcer that supports region staggering
        pb.withAnnouncer(new DelayedAnnouncer(jobCreator, vip, previousVersion) {
            @Override
            void announce(
                    PublishWorkflowContext ctx, String vip, long previousVersion, long currentVersion,
                    RegionEnum region) {
                boolean success = ctx.getVipAnnouncer()
                        .announce(vip, region, false, currentVersion, previousVersion);

                // Announce via Gutenberg using explicit announcers
                // This can be replaced when region staggering is supported in Gutenberg
                // However, the nostreams producer announcement still needs to be bound
                // to the main producer announcement
                ((NFHollowAnnouncer) ctx.getStateAnnouncer())
                        .announce(previousVersion, currentVersion, region);

                if (noStreamsVersion.getAsLong() == currentVersion) {
                    // No streams may not have published anything because there are no changes
                    ((NFHollowAnnouncer) ctx.getNostreamsStateAnnouncer())
                            .announce(noStreamsPreviousVersion.getAsLong(), currentVersion, region);
                }
                logResult(success, ctx, vip, currentVersion, region);
            }

            private void logResult(
                    boolean success, PublishWorkflowContext ctx,
                    String vip, long currentVersion,
                    NetflixConfiguration.RegionEnum region) {
                if (success) {
                    ctx.getLogger().info(AnnouncementSuccess, "Hollow data announce success: for version "
                            + currentVersion + " for vip " + vip + " region " + region);
                    ctx.getMetricRecorder().incrementCounter(TransformerMetricRecorder.Metric.AnnounceSuccess, 1,
                            "destination.region", region.toString());
                } else {
                    ctx.getLogger().error(AnnouncementFailure, "Hollow data announce failure: for version "
                            + currentVersion + " for vip " + vip + " region " + region);
                }
            }
        });
    }

    @Override
    public void initNoStreamsProducer(
            Supplier<CycleInputs> cycleInputs,
            CinderProducerBuilder pb,
            String vip, LongSupplier previousVersion) {
        pb.withListener(new HermesPublishListener(cycleInputs, jobCreator, vip, previousVersion));

        // Announcement occurs on the main producer
        // If there are no changes on the main producer but are on the nostreams producer
        // the no announcement will occur for either producer
        pb.withAnnouncer(v -> {});
    }
}
