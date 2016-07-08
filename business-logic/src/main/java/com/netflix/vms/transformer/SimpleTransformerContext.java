package com.netflix.vms.transformer;

import java.util.Set;
import java.util.function.Consumer;

import com.netflix.archaius.ConfigProxyFactory;
import com.netflix.archaius.config.EmptyConfig;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLoggers;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;

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
        this.config = new ConfigProxyFactory(EmptyConfig.INSTANCE).newProxy(TransformerConfig.class);
        this.files = files;
        this.recorder = recorder;
    }

    private long now = System.currentTimeMillis();
    private long currentCycleId;
    
    private Set<Integer> fastlaneIds;

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
    public TransformerCassandraHelper getCanaryResultsCassandraHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public TransformerCassandraHelper getValidationStatsCassandraHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public TransformerCassandraHelper getPoisonStatesHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
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
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }
}
