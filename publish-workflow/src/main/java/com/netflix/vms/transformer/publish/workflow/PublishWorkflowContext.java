package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobPublisher;

import com.netflix.aws.file.FileStore;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.poison.PoisonedStateMarker;
import com.netflix.vms.transformer.publish.status.PublishWorkflowStatusIndicator;
import java.util.function.Supplier;
import netflix.admin.videometadata.uploadstat.ServerUploadStatus;

public interface PublishWorkflowContext {

    PublishWorkflowContext withCurrentLoggerAndConfig();

    String getVip();

    TaggingLogger getLogger();

    TransformerConfig getConfig();

    TransformerCassandraHelper getCassandraHelper();

    PoisonedStateMarker getPoisonStateMarker();

    FileStore getFileStore();
    
    NetflixS3BlobPublisher getBlobPublisher();
    
    NetflixS3BlobPublisher getNostreamsBlobPublisher();

    VipAnnouncer getVipAnnouncer();

    long getNowMillis();

    OctoberSkyData getOctoberSkyData();

    CupLibrary getCupLibrary();

    TransformerMetricRecorder getMetricRecorder();

    Supplier<ServerUploadStatus> serverUploadStatus();

    PublishWorkflowStatusIndicator getStatusIndicator();
}
