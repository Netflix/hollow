package com.netflix.vms.transformer.context;

import com.netflix.archaius.api.Config;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLoggers;
import com.netflix.vms.transformer.TransformerCycleMonkey;
import com.netflix.vms.transformer.common.CycleMonkey;
import com.netflix.vms.transformer.common.TransformCycleInterrupter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistoryConsumer;
import com.netflix.vms.transformer.config.FrozenTransformerConfigFactory;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Properties go here.
 *
 */
public class TransformerServerContext implements TransformerContext {

    /* dependencies */
    private final TransformCycleInterrupter cycleInterrupter;
    private final TransformerCassandraHelper cassandraHelper;
    private final TransformerFiles files;
    private final PublicationHistoryConsumer publicationHistoryConsumer;
    private final TransformerMetricRecorder metricRecorder;
    private final OctoberSkyData octoberSkyData;
    private final CupLibrary cupLibrary;
    private final CycleMonkey cycleMonkey;

    private final FrozenTransformerConfigFactory configFactory;

    /* fields */
    private TransformerConfig staticConfig;
    private TransformerServerLogger logger;
    private long currentCycleId;
    private long now = System.currentTimeMillis();

    private Set<Integer> fastlaneIds;
    private Set<String> pinTitleSpecs;

    public TransformerServerContext(
            TransformCycleInterrupter cycleInterrupter,
            TransformerServerLogger logger,
            Config config,
            OctoberSkyData octoberSkyData,
            CupLibrary cupLibrary,
            TransformerMetricRecorder metricRecorder,
            TransformerCassandraHelper cassandraHelper,
            TransformerFiles files,
            PublicationHistoryConsumer publicationHistoryConsumer) {
        this.cycleInterrupter = cycleInterrupter;
        this.logger = logger;
        this.octoberSkyData = octoberSkyData;
        this.cupLibrary = cupLibrary;
        this.metricRecorder = metricRecorder;
        this.cassandraHelper = cassandraHelper;
        this.files = files;
        this.publicationHistoryConsumer = publicationHistoryConsumer;

        this.configFactory = new FrozenTransformerConfigFactory(config);
        this.staticConfig = configFactory.createStaticConfig(TaggingLoggers.sysoutLogger());
        this.cycleMonkey = new TransformerCycleMonkey(this);
    }

    @Override
    public void setCurrentCycleId(long currentCycleId) {
        this.currentCycleId = currentCycleId;
        this.logger = logger.withCurrentCycleId(currentCycleId);
    }

    @Override
    public void beginCycle() {
        TransformerContext.super.beginCycle();

        // freeze config for new cycle
        this.staticConfig = configFactory.createStaticConfig(logger);
        this.logger.info(TransformerLogTag.FreezeConfig, "Config Frozen");
    }

    @Override
    public long getCurrentCycleId() {
        return currentCycleId;
    }

    @Override
    public void setNowMillis(long now) {
        this.now = now;
    }

    @Override
    public long getNowMillis() {
        return now;
    }

    @Override
    public void setFastlaneIds(Set<Integer> fastlaneIds) {
        this.fastlaneIds = fastlaneIds;
    }

    @Override
    public Set<Integer> getFastlaneIds() {
        return fastlaneIds;
    }

    @Override
    public void setPinTitleSpecs(Set<String> specs) {
        this.pinTitleSpecs = specs;
    }

    @Override
    public Set<String> getPinTitleSpecs() {
        return this.pinTitleSpecs;
    }

    @Override
    public TaggingLogger getLogger() {
        return logger;
    }

    @Override
    public TransformerConfig getConfig() {
        return staticConfig;
    }

    @Override
    public TransformerMetricRecorder getMetricRecorder() {
        return metricRecorder;
    }

    @Override
    public TransformerCassandraHelper getCassandraHelper() {
        return cassandraHelper;
    }

    @Override
    public TransformerFiles files() {
        return files;
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        return publicationHistoryConsumer;
    }

    @Override
    public OctoberSkyData getOctoberSkyData() {
        return octoberSkyData;
    }

    @Override
    public CupLibrary getCupLibrary() {
        return cupLibrary;
    }

    @Override
    public TransformCycleInterrupter getCycleInterrupter() {
        return cycleInterrupter;
    }

    @Override
    public CycleMonkey getCycleMonkey() {
        return cycleMonkey;
    }

}