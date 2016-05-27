package com.netflix.vms.transformer.context;

import java.util.Map;

import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import java.io.IOException;
import com.netflix.archaius.ConfigProxyFactory;
import java.io.StringReader;
import java.util.Properties;
import java.util.Iterator;
import com.netflix.archaius.api.Config;
import com.netflix.archaius.config.MapConfig;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.util.Set;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import java.util.function.Consumer;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistory;
import com.netflix.vms.transformer.common.publish.workflow.PublicationHistoryConsumer;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;

/**
 * Properties go here.
 *
 */
public class TransformerServerContext implements TransformerContext {

    /* dependencies */
    private final TransformerCassandraHelper poisonStatesHelper;
    private final TransformerCassandraHelper hollowValidationStats;
    private final TransformerCassandraHelper canaryResults;
    private final TransformerFiles files;
    private final TransformerPlatformLibraries platformLibraries;
    private final PublicationHistoryConsumer publicationHistoryConsumer;
    private final TransformerMetricRecorder metricRecorder;
    private final Config config;

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
            TransformerCassandraHelper poisonStatesHelper,
            TransformerCassandraHelper hollowValidationStats,
            TransformerCassandraHelper canaryResults,
            TransformerFiles files,
            TransformerPlatformLibraries platformLibraries,
            PublicationHistoryConsumer publicationHistoryConsumer) {
        this.logger = logger;
        this.config = config;
        this.staticConfig = createStaticConfig();
        this.metricRecorder = metricRecorder;
        this.poisonStatesHelper = poisonStatesHelper;
        this.hollowValidationStats = hollowValidationStats;
        this.canaryResults = canaryResults;
        this.files = files;
        this.platformLibraries = platformLibraries;
        this.publicationHistoryConsumer = publicationHistoryConsumer;
    }

    @Override
    public void setCurrentCycleId(long currentCycleId) {
        this.currentCycleId = currentCycleId;
        this.logger = logger.withCurrentCycleId(currentCycleId);
        this.staticConfig = createStaticConfig();
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
	public TransformerConfig getConfig() {
		return staticConfig;
	}

    @Override
    public TransformerMetricRecorder getMetricRecorder() {
        return metricRecorder;
    }

    @Override
    public TransformerCassandraHelper getPoisonStatesHelper() {
        return poisonStatesHelper;
    }

    @Override
    public TransformerCassandraHelper getValidationStatsCassandraHelper() {
        return hollowValidationStats;
    }

    @Override
    public TransformerCassandraHelper getCanaryResultsCassandraHelper() {
        return canaryResults;
    }

    @Override
    public TransformerFiles files() {
        return files;
    }

    @Override
    public  TransformerPlatformLibraries platformLibraries() {
        return platformLibraries;
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        return publicationHistoryConsumer;
    }
    
    private TransformerConfig createStaticConfig() {
    	System.out.println(getPropertiesString());
    	
    	
    	Properties props = new Properties();
    	try {
			props.load(new StringReader(getPropertiesString()));
		} catch (IOException e) {
			logger.error(LogTag.ConfigurationFailure, "Failed to parse properties String: " + getPropertiesString());
		}
    	
    	/// log all property values
    	for(Map.Entry<Object, Object> entry : props.entrySet())
    		logger.info(LogTag.PropertyValue, "key=" + entry.getKey() + " value=" + entry.getValue());
    	
    	
    	return new ConfigProxyFactory(new MapConfig(props)).newProxy(TransformerConfig.class);
    }

    public String getPropertiesString() {
    	StringBuilder builder = new StringBuilder();
    	Iterator<String> iter = config.getKeys("vms.");
    	
    	while(iter.hasNext()) {
    		String key = iter.next();
			builder.append(key).append("=").append(config.getString(key)).append("\n");
    	}

    	return builder.toString();
    }

	@Override
	public OctoberSkyData getOctoberSkyData() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
