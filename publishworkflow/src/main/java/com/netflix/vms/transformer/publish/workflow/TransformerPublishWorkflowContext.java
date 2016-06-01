package com.netflix.vms.transformer.publish.workflow;

import com.netflix.vms.transformer.common.config.OctoberSkyData;

import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.TransformerPlatformLibraries;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.CassandraBasedPoisonedStateMarker;
import com.netflix.vms.transformer.publish.PoisonedStateMarker;

public class TransformerPublishWorkflowContext implements PublishWorkflowContext {

    /* dependencies */
    private final TransformerContext transformerCtx;
    private final TransformerConfig config;
    private final PoisonedStateMarker poisonStateMarker;
    private final VipAnnouncer vipAnnouncer;
    private final TransformerLogger logger;

    /* fields */
    private final String vip;
    private TransformerPlatformLibraries platform;

    public TransformerPublishWorkflowContext(TransformerContext ctx, TransformerPlatformLibraries platform, VipAnnouncer vipAnnouncer, String vip) {
        this(ctx, platform, vipAnnouncer, vip, new CassandraBasedPoisonedStateMarker(ctx, vip));
    }

    private TransformerPublishWorkflowContext(TransformerContext ctx, TransformerPlatformLibraries platform, VipAnnouncer vipAnnouncer, String vip, PoisonedStateMarker poisonStateMarker) {
        this.transformerCtx = ctx;
        this.platform = platform;
        this.vip = vip;
        this.config = ctx.getConfig();
        this.vipAnnouncer = vipAnnouncer;
        this.poisonStateMarker = poisonStateMarker;
        this.logger = ctx.getLogger();
    }

    public TransformerPublishWorkflowContext withCurrentLoggerAndConfig() {
        return new TransformerPublishWorkflowContext(transformerCtx, platform, vipAnnouncer, vip, poisonStateMarker);
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
    public TransformerConfig getConfig() {
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
        return platform.getFileStore();
    }

    @Override
    public VipAnnouncer getVipAnnouncer() {
        return vipAnnouncer;
    }

	@Override
	public OctoberSkyData getOctoberSkyData() {
		return transformerCtx.getOctoberSkyData();
	}

}
