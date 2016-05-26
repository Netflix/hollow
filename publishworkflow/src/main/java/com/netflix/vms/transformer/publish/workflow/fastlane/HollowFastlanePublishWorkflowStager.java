package com.netflix.vms.transformer.publish.workflow.fastlane;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.videometadata.publish.PublishRegionProvider;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowStager;
import com.netflix.vms.transformer.publish.workflow.TransformerPublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobDeleteFileJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJobScheduler;
import com.netflix.vms.transformer.publish.workflow.job.impl.FastlaneHermesAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.FileStoreHollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesBlobAnnouncer;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesTopicProvider;
import com.netflix.vms.transformer.publish.workflow.job.impl.HermesVipAnnouncer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HollowFastlanePublishWorkflowStager implements PublishWorkflowStager {

    private final PublicationJobScheduler scheduler;
    private final HollowBlobFileNamer fileNamer;
    private final Map<RegionEnum, FastlaneHermesAnnounceJob> priorAnnouncedJobs;
    
    private PublishWorkflowContext ctx;
    
    public HollowFastlanePublishWorkflowStager(TransformerContext ctx, String vip) {
        this.ctx = new TransformerPublishWorkflowContext(ctx, 
                new HermesVipAnnouncer(
                        new HermesBlobAnnouncer(ctx.platformLibraries().getHermesPublisher(),
                                HermesTopicProvider.HOLLOWBLOB_TOPIC_PREFIX),
                        ctx.platformLibraries().getHermesSubscriber(), null),
                vip);
        
        this.scheduler = new PublicationJobScheduler();
        this.fileNamer = new HollowBlobFileNamer(vip);
        this.priorAnnouncedJobs = new HashMap<RegionEnum, FastlaneHermesAnnounceJob>();
    }

    @Override
    public void triggerPublish(long previousVersion, long newVersion) {
    	
    	ctx = ctx.withCurrentLoggerAndConfig();

        // Add publish jobs
        final Map<RegionEnum, List<PublicationJob>> regionalPublishJobs = addPublishJobsAllRegions(previousVersion, newVersion);

        for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            createAnnounceJobForRegion(region, previousVersion, newVersion, regionalPublishJobs.get(region));
        }

        final List<PublicationJob> allPublishJobs = new ArrayList<>();
        for(final List<PublicationJob> list: regionalPublishJobs.values()){
            allPublishJobs.addAll(list);
        }
        addDeleteJob(previousVersion, newVersion, allPublishJobs);

    }

    private Map<RegionEnum, List<PublicationJob>> addPublishJobsAllRegions(long previousVersion, long newVersion){
        Map<RegionEnum, List<PublicationJob>> publishJobsByRegion = new HashMap<>(RegionEnum.values().length);
        for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            List<PublicationJob> allPublishJobs = new ArrayList<>();
            List<PublicationJob> publishJobs = addPublishJobs(region, previousVersion, newVersion);
            allPublishJobs.addAll(publishJobs);
            publishJobsByRegion.put(region, allPublishJobs);
        }
        return publishJobsByRegion;
    }

    private List<PublicationJob> addPublishJobs(RegionEnum region, long previousVersion, long newVersion) {
        File snapshotFile = new File(fileNamer.getSnapshotFileName(newVersion));
        File reverseDeltaFile = new File(fileNamer.getReverseDeltaFileName(newVersion, previousVersion));
        File deltaFile = new File(fileNamer.getDeltaFileName(previousVersion, newVersion));

        List<PublicationJob> submittedJobs = new ArrayList<>();
        if(snapshotFile.exists()){
            HollowBlobPublishJob publishJob = new FileStoreHollowBlobPublishJob(ctx, previousVersion, newVersion, PublishType.SNAPSHOT, region, snapshotFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        if(deltaFile.exists()){
            HollowBlobPublishJob publishJob = new FileStoreHollowBlobPublishJob(ctx, previousVersion, newVersion, PublishType.DELTA, region, deltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        if(reverseDeltaFile.exists()){
            HollowBlobPublishJob publishJob = new FileStoreHollowBlobPublishJob(ctx, previousVersion, newVersion, PublishType.REVERSEDELTA, region, reverseDeltaFile);
            scheduler.submitJob(publishJob);
            submittedJobs.add(publishJob);
        }
        return submittedJobs;

    }

    private FastlaneHermesAnnounceJob createAnnounceJobForRegion(RegionEnum region, long previousVerion, long newVersion, List<PublicationJob> publishJobsForRegion) {
        FastlaneHermesAnnounceJob announceJob = new FastlaneHermesAnnounceJob(ctx, previousVerion, newVersion, region, publishJobsForRegion, priorAnnouncedJobs.get(region));
        scheduler.submitJob(announceJob);

        priorAnnouncedJobs.put(region, announceJob);

        return announceJob;
    }

    private void addDeleteJob(long previousVersion, long nextVersion, List<PublicationJob> publishJobsForCycle) {
        scheduler.submitJob(
                new HollowBlobDeleteFileJob(ctx,
                							publishJobsForCycle,
                                            nextVersion,
                                            fileNamer.getDeltaFileName(previousVersion, nextVersion),
                                            fileNamer.getReverseDeltaFileName(nextVersion, previousVersion),
                                            fileNamer.getSnapshotFileName(nextVersion)));

    }
}
