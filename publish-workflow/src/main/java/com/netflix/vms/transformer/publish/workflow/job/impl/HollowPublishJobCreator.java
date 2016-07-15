package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
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
import java.util.Map;

public interface HollowPublishJobCreator {

    PublishWorkflowContext          beginStagingNewCycle();

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
                                                             long inputVersion,
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
    
    CreateDevSliceJob             createDevSliceJob         (PublishWorkflowContext ctx,
                                                             AnnounceJob dependency,
                                                             long inputVersion,
                                                             long cycleVersion);
}
