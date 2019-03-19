package com.netflix.vms.transformer.publish.workflow;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.CycleMonkey;
import com.netflix.vms.transformer.common.TransformerContext;
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

    TransformerContext getTransformerContext();

    PublishWorkflowContext withCurrentLoggerAndConfig();

    String getVip();

    TaggingLogger getLogger();

    TransformerConfig getConfig();

    TransformerCassandraHelper getCassandraHelper();

    PoisonedStateMarker getPoisonStateMarker();

    FileStore getFileStore();

    HollowProducer.Publisher getBlobPublisher();

    HollowProducer.Publisher getNostreamsBlobPublisher();

    HollowProducer.Announcer getStateAnnouncer();

    HollowProducer.Announcer getNostreamsStateAnnouncer();

    HollowProducer.Announcer getCanaryAnnouncer();

    HollowProducer.Publisher getDevSlicePublisher();

    HollowProducer.Announcer getDevSliceAnnouncer();

    VipAnnouncer getVipAnnouncer();

    long getNowMillis();

    OctoberSkyData getOctoberSkyData();

    CupLibrary getCupLibrary();

    TransformerMetricRecorder getMetricRecorder();

    Supplier<ServerUploadStatus> serverUploadStatus();

    PublishWorkflowStatusIndicator getStatusIndicator();

    CycleMonkey getCycleMonkey();
}
