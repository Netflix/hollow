package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.*;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob.PublishType;

public interface HollowPublishJobCreator {
    CircuitBreakerJob               createCircuitBreakerJob (String vip,
                                                             long newVersion,
                                                             File snapshotFile,
                                                             File deltaFile,
                                                             File reverseDeltaFile);

    BeforeCanaryAnnounceJob   createBeforeCanaryAnnounceJob (String vip,
                                                             long newVersion,
                                                             RegionEnum region,
                                                             CircuitBreakerJob circuitBreakerJob,
                                                             CanaryValidationJob previousCycleValidationJob,
                                                             List<PublicationJob> newPublishJobs,
                                                             CanaryRollbackJob previousCycleCanaryRoleBackJob);

    CanaryAnnounceJob               createCanaryAnnounceJob (String vip,
                                                             long newVersion,
                                                             RegionEnum region,
                                                             BeforeCanaryAnnounceJob beforeCanaryAnnounceHook,
                                                             CanaryValidationJob priorCycleCanaryValidationJob,
                                                             List<PublicationJob> newPublishJobs);

    CanaryRollbackJob               createCanaryRollbackJob (String vip,
                                                             long cycleVersion,
                                                             long previousVersion, CanaryValidationJob validationJob);

    CanaryValidationJob           createCanaryValidationJob (String vip,
                                                             long cycleVersion,
                                                             Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs,
                                                             Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs);

    AnnounceJob                           createAnnounceJob (String vip,
                                                             long priorVersion,
                                                             long newVersion,
                                                             RegionEnum region,
                                                             CanaryValidationJob validationJob,
                                                             DelayJob delayJob,
                                                             AnnounceJob previousAnnounceJob);

    HollowBlobPublishJob                   createPublishJob (String vip,
                                                             PublishType jobType,
                                                             long previousVersion,
                                                             long version,
                                                             RegionEnum region,
                                                             File fileToUpload);

    HollowBlobDeleteFileJob             createDeleteFileJob (List<PublicationJob> copyJobs,
                                                             long version,
                                                             String... filesToDelete);

    DelayJob                                 createDelayJob (PublicationJob dependency,
                                                             long delayMillis,
                                                             long cycleVersion);

    AutoPinbackJob                     createAutoPinbackJob (AnnounceJob announcement,
                                                             long waitMillis,
                                                             long cycleVersion);

    PoisonStateMarkerJob         createPoisonStateMarkerJob (PublicationJob validationJob,
                                                             long newVersion);

	  AfterCanaryAnnounceJob     createAfterCanaryAnnounceJob (String vip,
                                                             long newVersion,
                                                             RegionEnum region,
                                                             BeforeCanaryAnnounceJob beforeCanaryAnnounceJob,
                                                             CanaryAnnounceJob canaryAnnounceJob);
}
