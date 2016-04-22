package com.netflix.vms.transformer.publish.workflow;

import com.netflix.vms.transformer.publish.CassandraBasedPoisonedStateMarker;

import com.netflix.vms.transformer.util.VMSCassandraHelper;
import com.netflix.vms.transformer.common.TransformerLogger;

public class PublishWorkflowContext {

    private final String vip;
    private final TransformerLogger logger;
    private final PublishWorkflowConfig config;
    private final CassandraBasedPoisonedStateMarker poisonStateMarker;
    private final VMSCassandraHelper validationStatsCassandraHelper;
    private final VMSCassandraHelper canaryResultsCassandraHelper;

    public PublishWorkflowContext(String vip, TransformerLogger logger, PublishWorkflowConfig config, VMSCassandraHelper validationStatsCassandraHelper, VMSCassandraHelper canaryResultsCassandraHelper) {
        this.vip = vip;
        this.logger = logger;
        this.config = config;
        this.validationStatsCassandraHelper = validationStatsCassandraHelper;
        this.canaryResultsCassandraHelper = canaryResultsCassandraHelper;
        this.poisonStateMarker = new CassandraBasedPoisonedStateMarker(vip);
    }

    public String getVip() {
        return vip;
    }

    public TransformerLogger getLogger() {
        return logger;
    }

    public PublishWorkflowConfig getConfig() {
        return config;
    }

    public VMSCassandraHelper getValidationStatsCassandraHelper() {
        return validationStatsCassandraHelper;
    }

    public VMSCassandraHelper getCanaryResultsCassandraHelper() {
        return canaryResultsCassandraHelper;
    }

    public CassandraBasedPoisonedStateMarker getPoisonStateMarker() {
        return poisonStateMarker;
    }

}
