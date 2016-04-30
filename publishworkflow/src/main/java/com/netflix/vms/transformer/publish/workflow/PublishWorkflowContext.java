package com.netflix.vms.transformer.publish.workflow;

import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.VipAnnouncer;
import com.netflix.vms.transformer.publish.PoisonedStateMarker;

public interface PublishWorkflowContext {

    String getVip();

    TransformerLogger getLogger();

    PublishWorkflowConfig getConfig();

    TransformerCassandraHelper getValidationStatsCassandraHelper();

    TransformerCassandraHelper getCanaryResultsCassandraHelper();

    PoisonedStateMarker getPoisonStateMarker();

    FileStore getFileStore();

    VipAnnouncer getVipAnnouncer();

}
