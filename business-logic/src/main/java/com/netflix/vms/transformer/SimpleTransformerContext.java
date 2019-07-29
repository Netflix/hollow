package com.netflix.vms.transformer;

import com.netflix.archaius.ConfigProxyFactory;
import com.netflix.archaius.DefaultPropertyFactory;
import com.netflix.archaius.api.Config;
import com.netflix.archaius.config.EmptyConfig;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLoggers;
import com.netflix.vms.transformer.common.CycleMonkey;
import com.netflix.vms.transformer.common.TransformCycleInterrupter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Simple version of TransformerServerContext, used only in slicing and in certain unit tests.
 */
public class SimpleTransformerContext implements TransformerContext {

    private final TaggingLogger logger;
    private final TransformerConfig config;
    private final TransformerMetricRecorder recorder;
    private final TransformerFiles files;

    public SimpleTransformerContext() {
        this(TaggingLoggers.sysoutLogger(), new NoOpMetricRecorder(), null);
    }

    SimpleTransformerContext(TaggingLogger logger, TransformerMetricRecorder recorder, TransformerFiles files) {
        this.logger = logger;
        Config archaiusConfig = EmptyConfig.INSTANCE;
        this.config = new ConfigProxyFactory(archaiusConfig, archaiusConfig.getDecoder(), DefaultPropertyFactory.from(archaiusConfig)).newProxy(TransformerConfig.class);
        this.files = files;
        this.recorder = recorder;
    }

    private long now = System.currentTimeMillis();
    private long currentCycleId;

    private Set<Integer> fastlaneIds;
    private Set<String> pinnedTitleSpecs;

    @Override
    public void setCurrentCycleId(long cycleId) {
        this.currentCycleId = cycleId;
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
        this.pinnedTitleSpecs = specs;
    }

    @Override
    public Set<String> getPinTitleSpecs() {
        return this.pinnedTitleSpecs;
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
    public TransformerMetricRecorder getMetricRecorder() {
        return recorder;
    }

    @Override
    public TransformerCassandraHelper getCassandraHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't provide a CassandraHelper!");
    }

    @Override
    public TransformerFiles files() {
        return files;
    }

    @Override
    public OctoberSkyData getOctoberSkyData() {
        return SimpleOctoberSkyData.INSTANCE;
    }

    @Override
    public CupLibrary getCupLibrary() {
        return SimpleCupLibrary.INSTANCE;
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    /**
     * Override Supported Counties
     */
    public void overrideSupportedCountries(String... country) {
        if (country == null) throw new RuntimeException("country param can't be null");

        Set<String> countrySet = new HashSet<>(Arrays.asList(country));
        SimpleOctoberSkyData.INSTANCE.overrideSupportedCountries(countrySet);
    }

    @Override
    public TransformCycleInterrupter getCycleInterrupter() {
        return SimpleTransformCycleInterrupter.INSTANCE;
    }

    @Override
    public CycleMonkey getCycleMonkey() {
        return TransformerCycleMonkey.SIMPLE_CYCLE_MONKEY;
    }
}