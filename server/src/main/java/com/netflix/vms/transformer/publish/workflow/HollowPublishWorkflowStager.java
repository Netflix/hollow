package com.netflix.vms.transformer.publish.workflow;

import netflix.admin.videometadata.VMSPublishWorkflowHistoryAdmin;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.SysoutTransformerLogger;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.config.NetflixConfiguration.RegionEnum;
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
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublicationJobScheduler;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowPublishJobCreator;
import com.netflix.vms.transformer.servlet.platform.PlatformLibraries;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HollowPublishWorkflowStager {

	private final PublicationJobScheduler scheduler;
	private final HollowBlobFileNamer fileNamer;
	private final String vip;
	private PublishRegionProvider regionProvider;
	private final HollowPublishJobCreator jobCreator;
	private final Map<RegionEnum, AnnounceJob> priorAnnouncedJobs;
	private CanaryValidationJob priorCycleCanaryValidationJob;
	private CanaryRollbackJob priorCycleCanaryRollbackJob;

	public HollowPublishWorkflowStager(String vip, PublishWorkflowConfig config, TransformerLogger logger) {
	    this(vip, new DefaultHollowPublishJobCreator(vip, config, logger), PlatformLibraries.ASTYANAX, logger);
	}

	public HollowPublishWorkflowStager(String vip, HollowPublishJobCreator jobCreator, NFAstyanaxManager astyanaxManager, TransformerLogger logger){
		this.scheduler = new PublicationJobScheduler();
		this.fileNamer = new HollowBlobFileNamer(vip);
		this.vip = vip;
		this.regionProvider =  new PublishRegionProvider(new SysoutTransformerLogger());
		this.priorAnnouncedJobs = new HashMap<RegionEnum, AnnounceJob>();
		this.jobCreator = jobCreator;

        exposePublicationHistory();
    }

	public void triggerPublish(long previousVersion, long newVersion) {
		// Add validation job
		final CircuitBreakerJob circuitBreakerJob = addCircuitBreakerJob(previousVersion, newVersion);

		// Add publish jobs
		final Map<RegionEnum, List<PublicationJob>> addPublishJobsAllRegions = addPublishJobsAllRegions(previousVersion, newVersion, circuitBreakerJob);

		/// add canary announcement and validation jobs
		final CanaryValidationJob canaryValidationJob = addCanaryJobs(previousVersion, newVersion, circuitBreakerJob, addPublishJobsAllRegions);

		final AnnounceJob primaryRegionAnnounceJob = createAnnounceJobForRegion(regionProvider.getPrimaryRegion(), previousVersion, newVersion, canaryValidationJob, null);

		/// add secondary regions
		for(final RegionEnum region : regionProvider.getNonPrimaryRegions()) {
		    createAnnounceJobForRegion(region, previousVersion, newVersion, canaryValidationJob, primaryRegionAnnounceJob);
		}

		final List<PublicationJob> allPublishJobs = new ArrayList<>();
		for(final List<PublicationJob> list: addPublishJobsAllRegions.values()){
			allPublishJobs.addAll(list);
		}
		addDeleteJob(previousVersion, newVersion, allPublishJobs);

        priorCycleCanaryValidationJob = canaryValidationJob;
    }

	private AnnounceJob createAnnounceJobForRegion(RegionEnum region, long previousVerion, long newVersion, CanaryValidationJob validationJob, AnnounceJob primaryRegionAnnounceJob) {
	    DelayJob delayJob = jobCreator.createDelayJob(primaryRegionAnnounceJob, regionProvider.getPublishDelayInSeconds(region) * 1000, newVersion);
	    scheduler.submitJob(delayJob);

	    AnnounceJob announceJob = jobCreator.createAnnounceJob(vip, previousVerion, newVersion, region, validationJob, delayJob, priorAnnouncedJobs.get(region));
	    scheduler.submitJob(announceJob);

        priorAnnouncedJobs.put(region, announceJob);

	    if(primaryRegionAnnounceJob == null) {
	        /// this is the primary region, create an auto pinback job
	        AutoPinbackJob autoPinbackJob = jobCreator.createAutoPinbackJob(announceJob, 300000L, newVersion);
	        scheduler.submitJob(autoPinbackJob);
	    }

        return announceJob;
    }

	private Map<RegionEnum, List<PublicationJob>> addPublishJobsAllRegions(long previousVersion, long newVersion, CircuitBreakerJob circuitBreakerJob){
		Map<RegionEnum, List<PublicationJob>> publishJobsByRegion = new HashMap<>(RegionEnum.values().length);
	    for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
	    	List<PublicationJob> allPublishJobs = new ArrayList<>();
	        List<PublicationJob> publishJobs = addPublishJobs(region, previousVersion, newVersion);
	        allPublishJobs.addAll(publishJobs);
	        publishJobsByRegion.put(region, allPublishJobs);
	    }
	    return publishJobsByRegion;
	}

	private CanaryValidationJob addCanaryJobs(long previousVersion, long newVersion, CircuitBreakerJob circuitBreakerJob, Map<RegionEnum, List<PublicationJob>> publishJobsByRegion) {
	    Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs = new HashMap<RegionEnum, BeforeCanaryAnnounceJob>(3);
	    Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs = new HashMap<RegionEnum, AfterCanaryAnnounceJob>(3);

	    for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
	    	BeforeCanaryAnnounceJob beforeCanaryAnnounceJob = jobCreator.createBeforeCanaryAnnounceJob(vip, newVersion, region, circuitBreakerJob,
	    														priorCycleCanaryValidationJob, publishJobsByRegion.get(region), priorCycleCanaryRollbackJob);
	    	scheduler.submitJob(beforeCanaryAnnounceJob);

	    	CanaryAnnounceJob canaryAnnounceJob = jobCreator.createCanaryAnnounceJob(vip, newVersion, region, beforeCanaryAnnounceJob, priorCycleCanaryValidationJob, publishJobsByRegion.get(region));
	        scheduler.submitJob(canaryAnnounceJob);

	        AfterCanaryAnnounceJob afterCanaryAnnounceJob = jobCreator.createAfterCanaryAnnounceJob(vip, newVersion, region, beforeCanaryAnnounceJob, canaryAnnounceJob);
	        scheduler.submitJob(afterCanaryAnnounceJob);

	        beforeCanaryAnnounceJobs.put(region, beforeCanaryAnnounceJob);
	        afterCanaryAnnounceJobs.put(region, afterCanaryAnnounceJob);
	    }

	    CanaryValidationJob validationJob = jobCreator.createCanaryValidationJob(vip, newVersion, beforeCanaryAnnounceJobs, afterCanaryAnnounceJobs);
	    PoisonStateMarkerJob canaryPoisonStateMarkerJob = jobCreator.createPoisonStateMarkerJob(validationJob, newVersion);
	    CanaryRollbackJob canaryRollbackJob = jobCreator.createCanaryRollbackJob(vip, newVersion, previousVersion, validationJob);

        scheduler.submitJob(validationJob);
        scheduler.submitJob(canaryPoisonStateMarkerJob);
        scheduler.submitJob(canaryRollbackJob);

	    this.priorCycleCanaryRollbackJob = canaryRollbackJob;// this is used in next cycle for dependency wiring.
	    return validationJob;
	}

	private CircuitBreakerJob addCircuitBreakerJob(long previousVersion, long newVersion) {
		File snapshotFile = new File(fileNamer.getSnapshotFileName(newVersion));
		File deltaFile = new File(fileNamer.getDeltaFileName(previousVersion, newVersion));
		File reverseDeltaFile = new File(fileNamer.getReverseDeltaFileName(newVersion, previousVersion));

		CircuitBreakerJob validationJob = jobCreator.createCircuitBreakerJob(vip, newVersion, snapshotFile, deltaFile, reverseDeltaFile);
		scheduler.submitJob(validationJob);

		PoisonStateMarkerJob poisonMarkerJob = jobCreator.createPoisonStateMarkerJob(validationJob, newVersion);
		scheduler.submitJob(poisonMarkerJob);

        return validationJob;
    }

	private void addDeleteJob(long previousVersion, long nextVersion, List<PublicationJob> publishJobsForCycle) {
		scheduler.submitJob(
		        jobCreator.createDeleteFileJob(publishJobsForCycle,
                                				nextVersion,
                                				fileNamer.getDeltaFileName(previousVersion, nextVersion),
                                				fileNamer.getReverseDeltaFileName(nextVersion, previousVersion),
                                				fileNamer.getSnapshotFileName(nextVersion)));

    }

	private List<PublicationJob> addPublishJobs(RegionEnum region, long previousVersion, long newVersion) {
	    File snapshotFile = new File(fileNamer.getSnapshotFileName(newVersion));
	    File reverseDeltaFile = new File(fileNamer.getReverseDeltaFileName(newVersion, previousVersion));
	    File deltaFile = new File(fileNamer.getDeltaFileName(previousVersion, newVersion));

	    List<PublicationJob> submittedJobs = new ArrayList<>();
        if(snapshotFile.exists()){
			HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.SNAPSHOT, previousVersion, newVersion, region, snapshotFile);
			scheduler.submitJob(publishJob);
			submittedJobs.add(publishJob);
		}
        if(deltaFile.exists()){
			HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.DELTA, previousVersion, newVersion, region, deltaFile);
			scheduler.submitJob(publishJob);
			submittedJobs.add(publishJob);
		}
        if(reverseDeltaFile.exists()){
			HollowBlobPublishJob publishJob = jobCreator.createPublishJob(vip, PublishType.REVERSEDELTA, previousVersion, newVersion, region, reverseDeltaFile);
			scheduler.submitJob(publishJob);
			submittedJobs.add(publishJob);
		}
		return submittedJobs;

    }

    PublicationJobScheduler getExecutor() {
        return scheduler;
    }

	void injectPublishRegionProvider(PublishRegionProvider regionProvider) {
		this.regionProvider = regionProvider;
	}

    private void exposePublicationHistory() {
        VMSPublishWorkflowHistoryAdmin.history = scheduler.getHistory();
    }

}
