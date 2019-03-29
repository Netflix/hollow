package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.DefaultHollowPublishJobCreator;
import com.netflix.vms.transformer.publish.workflow.job.impl.FileStoreHollowBlobPublishJob;
import java.time.Duration;
import java.util.function.LongSupplier;

public class HermesPublishListener implements
        RestoreListener,
        PublishListener {
    private final LongSupplier inputVersion;
    private final DefaultHollowPublishJobCreator jobCreator;
    private final String vip;
    private final PublishWorkflowContext ctx;

    public HermesPublishListener(
            LongSupplier inputVersion,
            DefaultHollowPublishJobCreator jobCreator,
            String vip) {
        this.inputVersion = inputVersion;
        this.jobCreator = jobCreator;
        this.vip = vip;
        this.ctx = jobCreator.getContext();
    }

    private long previousVersion;

    private long currentVersion;


    // RestoreListener

    @Override
    public void onProducerRestoreStart(long restoreVersion) {
        previousVersion = Long.MIN_VALUE;
    }

    @Override
    public void onProducerRestoreComplete(Status status, long versionDesired, long versionReached, Duration elapsed) {
        previousVersion = versionReached;
    }


    // PublishListener

    @Override public void onNoDeltaAvailable(long version) {
    }

    @Override public void onPublishStart(long version) {
        currentVersion = version;
    }

    @Override
    public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
        HollowBlobPublishJob.PublishType pt;
        switch (blob.getType()) {
            case SNAPSHOT:
                pt = HollowBlobPublishJob.PublishType.SNAPSHOT;
                break;
            case DELTA:
                pt = HollowBlobPublishJob.PublishType.DELTA;
                break;
            case REVERSE_DELTA:
            default:
                pt = HollowBlobPublishJob.PublishType.REVERSEDELTA;
                break;
        }

        // @@@ Require a onBlobStaged event, since the snapshot may be published asynchronously (it is not by
        //     default for a producer)
        FileStoreHollowBlobPublishJob publishJob = (FileStoreHollowBlobPublishJob) jobCreator.createPublishJob(
                vip, pt, false,
                inputVersion.getAsLong(), previousVersion, currentVersion, blob.getPath().toFile());
        publishJob.executeJob(false, elapsed.toMillis());
    }

    @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
    }
}
