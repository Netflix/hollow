package com.netflix.vms.transformer.publish.workflow;

import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.CassandraBasedPoisonedStateMarker;
import com.netflix.vms.transformer.publish.PoisonedStateMarker;

public class TransformerPublishWorkflowContext implements PublishWorkflowContext {

    /* dependencies */
    private final TransformerContext transformerCtx;
    private final PublishWorkflowConfig config;
    private final PoisonedStateMarker poisonStateMarker;
    private final VipAnnouncer vipAnnouncer;
    private final TransformerLogger logger;

    /* fields */
    private final String vip;

    public TransformerPublishWorkflowContext(TransformerContext ctx, PublishWorkflowConfig config, VipAnnouncer vipAnnouncer, String vip) {
        this.transformerCtx = ctx;
        this.logger = ctx.getLogger();
        this.vip = vip;
        this.config = config;
        this.vipAnnouncer = vipAnnouncer;
        this.poisonStateMarker = new CassandraBasedPoisonedStateMarker(ctx, vip);
    }

    @Override
    public String getVip() {
        return vip;
    }

    @Override
    public TransformerLogger getLogger() {
        return logger;
    }

    @Override
    public PublishWorkflowConfig getConfig() {
        return config;
    }

    @Override
    public TransformerCassandraHelper getValidationStatsCassandraHelper() {
        return transformerCtx.getValidationStatsCassandraHelper();
    }

    @Override
    public TransformerCassandraHelper getCanaryResultsCassandraHelper() {
        return transformerCtx.getCanaryResultsCassandraHelper();
    }

    @Override
    public PoisonedStateMarker getPoisonStateMarker() {
        return poisonStateMarker;
    }

    @Override
    public FileStore getFileStore() {
        return transformerCtx.platformLibraries().getFileStore();
    }

    @Override
    public VipAnnouncer getVipAnnouncer() {
        return vipAnnouncer;
    }
}
