package com.netflix.vms.transformer;

import java.util.Set;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;
import java.util.function.Consumer;

public class SimpleTransformerContext implements TransformerContext {

    private final TransformerLogger logger;
    private final TransformerMetricRecorder recorder;
    private final TransformerFiles files;

    public SimpleTransformerContext() {
        this(new SysoutTransformerLogger(), new NoOpMetricRecorder(), null);
    }

    SimpleTransformerContext(TransformerLogger logger, TransformerMetricRecorder recorder, TransformerFiles files) {
        this.logger = logger;
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
    public TransformerLogger getLogger() {
        return logger;
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
    public TransformerPlatformLibraries platformLibraries() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }
}
