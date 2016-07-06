package com.netflix.vms.transformer.publish.workflow;

import java.util.function.Supplier;

import com.netflix.aws.file.FileStore;
import com.netflix.vms.io.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.poison.CassandraBasedPoisonedStateMarker;
import com.netflix.vms.transformer.publish.poison.PoisonedStateMarker;
import com.netflix.vms.transformer.publish.status.PublishWorkflowStatusIndicator;

import netflix.admin.videometadata.uploadstat.ServerUploadStatus;

public class TransformerPublishWorkflowContext implements PublishWorkflowContext {

    /* dependencies */
    private final TransformerContext transformerCtx;
    private final TransformerConfig config;
    private final PoisonedStateMarker poisonStateMarker;
    private final VipAnnouncer vipAnnouncer;
    private final TaggingLogger logger;
    private final Supplier<ServerUploadStatus> uploadStatus;
    private final FileStore fileStore;
    private final PublishWorkflowStatusIndicator statusIndicator;

    /* fields */
    private final String vip;
	private final long nowMillis;

    public TransformerPublishWorkflowContext(TransformerContext ctx, VipAnnouncer vipAnnouncer, Supplier<ServerUploadStatus> uploadStatus, FileStore fileStore, String vip) {
        this(ctx, vipAnnouncer, uploadStatus, fileStore, vip, new CassandraBasedPoisonedStateMarker(ctx, vip));
    }

    private TransformerPublishWorkflowContext(TransformerContext ctx, VipAnnouncer vipAnnouncer, Supplier<ServerUploadStatus> uploadStatus, FileStore fileStore, String vip, PoisonedStateMarker poisonStateMarker) {
        this.transformerCtx = ctx;
        this.vip = vip;
        this.config = ctx.getConfig();
        this.vipAnnouncer = vipAnnouncer;
        this.poisonStateMarker = poisonStateMarker;
        this.uploadStatus = uploadStatus;
        this.fileStore = fileStore;
        this.statusIndicator = new PublishWorkflowStatusIndicator(ctx.getMetricRecorder());
        this.logger = ctx.getLogger();
        this.nowMillis = ctx.getNowMillis();
    }

    public TransformerPublishWorkflowContext withCurrentLoggerAndConfig() {
        return new TransformerPublishWorkflowContext(transformerCtx, vipAnnouncer, uploadStatus, fileStore, vip, poisonStateMarker);
    }

    @Override
    public String getVip() {
        return vip;
    }

    @Override
    public TaggingLogger getLogger() {
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
        return fileStore;
    }

    @Override
    public VipAnnouncer getVipAnnouncer() {
        return vipAnnouncer;
    }

	@Override
	public long getNowMillis() {
		return nowMillis;
	}

	public OctoberSkyData getOctoberSkyData() {
		return transformerCtx.getOctoberSkyData();
	}

	@Override
	public TransformerMetricRecorder getMetricRecorder() {
		return transformerCtx.getMetricRecorder();
	}

	public Supplier<ServerUploadStatus> serverUploadStatus() {
	    return uploadStatus;
	}

    @Override
    public PublishWorkflowStatusIndicator getStatusIndicator() {
        return statusIndicator;
    }
}
