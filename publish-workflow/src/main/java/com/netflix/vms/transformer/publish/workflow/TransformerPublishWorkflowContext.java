package com.netflix.vms.transformer.publish.workflow;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.producer.HollowProducer.Announcer;
import com.netflix.hollow.api.producer.HollowProducer.Publisher;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.poison.CassandraBasedPoisonedStateMarker;
import com.netflix.vms.transformer.publish.poison.PoisonedStateMarker;
import com.netflix.vms.transformer.publish.status.PublishWorkflowStatusIndicator;
import java.util.function.Supplier;
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
    private final Publisher publisher;
    private final Publisher nostreamsPublisher;
    private final Announcer announcer;
    private final PublishWorkflowStatusIndicator statusIndicator;

    /* fields */
    private final String vip;
    private final long nowMillis;

    public TransformerPublishWorkflowContext(TransformerContext ctx, VipAnnouncer vipAnnouncer, Supplier<ServerUploadStatus> uploadStatus, FileStore fileStore, Publisher publisher, Publisher nostreamsPublisher, Announcer announcer, String vip) {
        this(ctx, vipAnnouncer, uploadStatus, new PublishWorkflowStatusIndicator(ctx.getMetricRecorder()), fileStore, publisher, nostreamsPublisher, announcer, vip, new CassandraBasedPoisonedStateMarker(ctx, vip));
    }

    private TransformerPublishWorkflowContext(TransformerContext ctx, VipAnnouncer vipAnnouncer, Supplier<ServerUploadStatus> uploadStatus, PublishWorkflowStatusIndicator statusIndicator, FileStore fileStore, Publisher publisher, Publisher nostreamsPublisher, Announcer announcer, String vip, PoisonedStateMarker poisonStateMarker) {
        this.transformerCtx = ctx;
        this.vip = vip;
        this.config = ctx.getConfig();
        this.vipAnnouncer = vipAnnouncer;
        this.poisonStateMarker = poisonStateMarker;
        this.uploadStatus = uploadStatus;
        this.fileStore = fileStore;
        this.publisher = publisher;
        this.nostreamsPublisher = nostreamsPublisher;
        this.announcer = announcer;
        this.statusIndicator = statusIndicator;
        this.logger = ctx.getLogger();
        this.nowMillis = ctx.getNowMillis();
    }

    @Override
    public TransformerPublishWorkflowContext withCurrentLoggerAndConfig() {
        return new TransformerPublishWorkflowContext(transformerCtx, vipAnnouncer, uploadStatus, statusIndicator, fileStore, publisher, nostreamsPublisher, announcer, vip, poisonStateMarker);
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
    public TransformerCassandraHelper getCassandraHelper() {
        return transformerCtx.getCassandraHelper();
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
    public Publisher getBlobPublisher() {
        return publisher;
    }
    
    @Override
    public Publisher getNostreamsBlobPublisher() {
        return nostreamsPublisher;
    }

    @Override
    public Announcer getStateAnnouncer() {
        return announcer;
    }

    @Override
    public VipAnnouncer getVipAnnouncer() {
        return vipAnnouncer;
    }

    @Override
    public long getNowMillis() {
        return nowMillis;
    }

    @Override
    public OctoberSkyData getOctoberSkyData() {
        return transformerCtx.getOctoberSkyData();
    }

    @Override
    public CupLibrary getCupLibrary() {
        return transformerCtx.getCupLibrary();
    }

    @Override
    public TransformerMetricRecorder getMetricRecorder() {
        return transformerCtx.getMetricRecorder();
    }

    @Override
    public Supplier<ServerUploadStatus> serverUploadStatus() {
        return uploadStatus;
    }

    @Override
    public PublishWorkflowStatusIndicator getStatusIndicator() {
        return statusIndicator;
    }
}
