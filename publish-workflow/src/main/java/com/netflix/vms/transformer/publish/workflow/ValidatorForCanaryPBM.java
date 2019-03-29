package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.hollow.api.producer.validation.ValidationResult;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.PoisonStateMarkerJob;
import com.netflix.vms.transformer.publish.workflow.job.framework.PublishWorkflowPublicationJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.CassandraCanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowBlobAfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowBlobBeforeCanaryAnnounceJob;
import java.time.Duration;
import java.util.Collections;

public class ValidatorForCanaryPBM implements
        RestoreListener,
        ValidationStatusListener,
        ValidatorListener {
    private final ValidatorForCircuitBreakers vcbs;
    private final PublishWorkflowStager publishStager;
    private final DefaultHollowPublishJobCreator jobCreator;
    private final String vip;

    public ValidatorForCanaryPBM(
            ValidatorForCircuitBreakers vcbs,
            PublishWorkflowStager publishStager,
            DefaultHollowPublishJobCreator jobCreator,
            String vip) {
        this.vcbs = vcbs;
        this.publishStager = publishStager;
        this.jobCreator = jobCreator;
        this.vip = vip;
    }


    // RestoreListener

    private long previousVersion;

    @Override
    public void onProducerRestoreStart(long restoreVersion) {
        previousVersion = Long.MIN_VALUE;
    }

    @Override
    public void onProducerRestoreComplete(
            Status status, long versionDesired, long versionReached, Duration elapsed) {
        previousVersion = versionReached;
    }


    // ValidationStatusListener

    @Override
    public void onValidationStatusStart(long version) {
    }

    @Override
    public void onValidationStatusComplete(
            ValidationStatus status, long version, Duration elapsed) {
        if (status.passed()) {
            return;
        }

        // Create Fake job to avoid NPE when constructing CassandraPoisonStateMarkerJob
        // (whose job name is derived from its dependent job name)
        PublicationJob fj = new PublishWorkflowPublicationJob(jobCreator.getContext(), "validation", version) {
            @Override public boolean executeJob() {
                return false;
            }

            @Override protected boolean isFailedBasedOnDependencies() {
                return false;
            }

            @Override public boolean isEligible() {
                return false;
            }
        };
        PoisonStateMarkerJob canaryPoisonStateMarkerJob = jobCreator.createPoisonStateMarkerJob(fj, version);
        canaryPoisonStateMarkerJob.executeJob();
    }


    // ValidatorListener

    @Override
    public String getName() {
        return "Canary Playback Monkey";
    }

    @Override
    public ValidationResult onValidate(
            HollowProducer.ReadState readState) {
        if (vcbs.failed) {
            return ValidationResult.from(this).passed("SKIPPED: circuit breaker validators failed");
        }

        long version = readState.getVersion();

        ValidationResult r = null;
        try {
            r = validate(readState, vip, version);
            return r;
        } finally {
            if (r == null || !r.isPassed()) {
                CanaryRollbackJob canaryRollbackJob = jobCreator.createCanaryRollbackJob(
                        vip, version, previousVersion,
                        null);
                canaryRollbackJob.executeJob();
            }
        }
    }

    private ValidationResult validate(HollowProducer.ReadState readState, String vip, long version) {
        HollowReadStateEngine readStateEngine = readState.getStateEngine();

        HollowBlobBeforeCanaryAnnounceJob beforeCanaryAnnounceJob =
                (HollowBlobBeforeCanaryAnnounceJob) jobCreator.createBeforeCanaryAnnounceJob(
                        vip, version,
                        null, Collections.emptyList());
        if (!beforeCanaryAnnounceJob.executeJob(readStateEngine)) {
            return ValidationResult.from(this).failed(beforeCanaryAnnounceJob.toString());
        }

        CanaryAnnounceJob canaryAnnounceJob = jobCreator.createCanaryAnnounceJob(
                vip, version,
                beforeCanaryAnnounceJob);
        if (!canaryAnnounceJob.executeJob()) {
            return ValidationResult.from(this).failed(canaryAnnounceJob.toString());
        }

        HollowBlobAfterCanaryAnnounceJob afterCanaryAnnounceJob =
                (HollowBlobAfterCanaryAnnounceJob) jobCreator.createAfterCanaryAnnounceJob(
                        vip, version,
                        canaryAnnounceJob);
        if (!afterCanaryAnnounceJob.executeJob(readStateEngine)) {
            return ValidationResult.from(this).failed(afterCanaryAnnounceJob.toString());
        }

        CassandraCanaryValidationJob validationJob =
                (CassandraCanaryValidationJob) jobCreator.createCanaryValidationJob(
                        vip, version,
                        beforeCanaryAnnounceJob, afterCanaryAnnounceJob);
        if (!validationJob.executeJob(readStateEngine)) {
            return ValidationResult.from(this).failed(validationJob.toString());
        }

        return ValidationResult.from(this).passed();
    }
}
