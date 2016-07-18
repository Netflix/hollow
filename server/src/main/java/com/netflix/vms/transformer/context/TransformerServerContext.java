package com.netflix.vms.transformer.context;

import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;

import java.util.Set;
import java.util.function.Consumer;
import com.netflix.archaius.api.Config;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.logging.TaggingLoggers;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistoryConsumer;
import com.netflix.vms.transformer.config.FrozenTransformerConfigFactory;
import com.netflix.vms.transformer.logger.TransformerServerLogger;

/**
 * Properties go here.
 *
 */
public class TransformerServerContext implements TransformerContext {

    /* dependencies */
    private final TransformerCassandraHelper cassandraHelper;
    private final TransformerFiles files;
    private final PublicationHistoryConsumer publicationHistoryConsumer;
    private final TransformerMetricRecorder metricRecorder;
    private final OctoberSkyData octoberSkyData;

    private final FrozenTransformerConfigFactory configFactory; 
    
    /* fields */
    private TransformerConfig staticConfig;
    private TransformerServerLogger logger;
    private long currentCycleId;
    private long now = System.currentTimeMillis();
    
    private Set<Integer> fastlaneIds;

    public TransformerServerContext(
            TransformerServerLogger logger,
            Config config,
            OctoberSkyData octoberSkyData,
            TransformerMetricRecorder metricRecorder,
            TransformerCassandraHelper cassandraHelper,
            TransformerFiles files,
            PublicationHistoryConsumer publicationHistoryConsumer) {
        this.logger = logger;
        this.octoberSkyData = octoberSkyData;
        this.metricRecorder = metricRecorder;
        this.cassandraHelper = cassandraHelper;
        this.files = files;
        this.publicationHistoryConsumer = publicationHistoryConsumer;
        
        this.configFactory = new FrozenTransformerConfigFactory(config);
        this.staticConfig = configFactory.createStaticConfig(TaggingLoggers.sysoutLogger());
    }

    @Override
    public void setCurrentCycleId(long currentCycleId) {
        this.currentCycleId = currentCycleId;
        this.logger = logger.withCurrentCycleId(currentCycleId);
        this.staticConfig = configFactory.createStaticConfig(logger);
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
}
