package com.netflix.vms.transformer.common.config;

import com.netflix.archaius.api.annotations.Configuration;
import com.netflix.archaius.api.annotations.DefaultValue;
import com.netflix.archaius.api.annotations.PropertyName;

/**
 * This is the root config of the service. Simply inject it to constructors of any objects you
 * need to configure. To benefit from dynamic properties simply call the method to retrieve the
 * property value in your "hot path" code. No need to store it in a local or class variable.
 *
 * Check out the application.properties file to see how they can be set as properties.
 *
 * @author Feel free to modify.
 */
@Configuration(prefix = "vms")
public interface TransformerConfig {

    @DefaultValue("defaultConverterVip")
    String getConverterVip();

    @DefaultValue("defaultTransformerVip")
    String getTransformerVip();

    String getNetflixEnvironment();

    String getAwsRegion();

    String getAwsInstanceId();

    String getAwsAmiId();

    Long getPinInputVersion();

    Long getNowMillis();

    String getFollowVip();

    @DefaultValue("15")
    int getMinCycleCadenceMinutes();

    @DefaultValue("0")
    int getMaxTolerableFailedTransformerHierarchies();

    String getOverrideFastlaneIds();

    // FORMAT: topNodeId:version or topNodeId:version:[in|out] - in=input pinning; otherwise, output slicing
    String getOverrideTitleSpecs();

    @DefaultValue("false")
    boolean isRestoreFromPreviousStateEngine();

    //////////////// PUBLISH WORKFLOW ///////////////////

    @DefaultValue("true")
    public boolean isCircuitBreakersEnabled();

    @DefaultValue("true")
    @PropertyName(name="vms.circuitBreakerEnabled.${0}")
    public boolean isCircuitBreakerEnabled(String ruleName);

    @PropertyName(name="vms.circuitBreakerEnabled.${0}.${1}")
    public Boolean isCircuitBreakerEnabled(String ruleName, String country);

    @DefaultValue("5.0")
    @PropertyName(name="vms.circuitBreakerThreshold.${0}")
    public double getCircuitBreakerThreshold(String ruleName);

    @PropertyName(name="vms.circuitBreakerThreshold.${0}.${1}")
    public Double getCircuitBreakerThreshold(String ruleName, String country);

    @DefaultValue("false")
    public boolean isPlaybackMonkeyEnabled();

    @DefaultValue("AU,BR,GB,JP,NL,US,CA,CH,DE,MX")
    public String getPlaybackMonkeyTestForCountries();

    @DefaultValue("15000")
    public int getPlaybackMonkeyMaxTestVideosSize();

    @DefaultValue("true")
    public boolean shouldFailCycleOnPlaybackMonkeyFailure();

    @DefaultValue("true")
    public boolean shouldProcessExtraNonVideoGeneralVideoIds();

    @DefaultValue("true")
    public boolean isEnableCdnDirectoryOptimization();

    @DefaultValue("5")
    public int getComputedCdnFolderLength();

    @DefaultValue("ja-Hira,ja-Kana,ja-Latn")
    public String getTransliteratedPersonLocales();

    @DefaultValue("5")
    public int getPlaybackMonkeyMaxRetriesPerTest();

    @DefaultValue("")
    String getPlaybackMonkeyVideoCountryToExclude();

    @DefaultValue("10.0f")
    public float getPlaybackMonkeyNoiseTolerance();

    @DefaultValue("0.1f")
    public float getPlaybackmonkeyMissingViewShareThreshold();

    @DefaultValue("false")
    public boolean isBigGreenButton();

    @DefaultValue("false")
    public boolean isCreateDevSlicedBlob();


    /////////////// VMS IOPS (ElasticSearch) ///////////////

    @DefaultValue("true")
    boolean isElasticSearchLoggingEnabled();

    @DefaultValue("es_vmsops")
    String getElasticSearchClusterName();

    @DefaultValue("7102")
    String getElasticSearchDataPort();

    @DefaultValue("7104")
    String getElasticSearchHttpPort();

    @DefaultValue("50s")
    String getElasticSearchNodesSamplerIntervalInSeconds();

    @DefaultValue("true")
    String getElasticSearchTcpCompress();

    @DefaultValue("50s")
    String getElasticSearchTimeoutInSeconds();

    @DefaultValue("50s")
    String getElasticSearchSamplerIntervalInSeconds();

    @DefaultValue("25000")
    int getElasticSearchMaxQueueSize();

    @DefaultValue("6")
    int getElasticSearchMaxTransportThreads();

    @DefaultValue("30000")
    int getElasticSearchQueueTimeoutMillis();

    @DefaultValue("true")
    boolean isElasticSearchNoWaitingEnabled();

}
