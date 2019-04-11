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

    @DefaultValue("false") // Master switch for CycleMonkey
    boolean isTransformerCycleMonkeyEnabled();

    @DefaultValue("true") // Enable by default if CycleMonkey is enabled - turns on auto failure on odd cycles
    boolean isTransformerCycleMonkeyAutoChaosEnabled();

    @DefaultValue("true") // Enable by default if AutoChaos is enabled - toggle auto failure at phase level
    @PropertyName(name = "vms.transformerCycleMonkeyAutoPhaseChaosEnabled.${0}")
    public boolean isTransformerCycleMonkeyAutoPhaseChaosEnabled(String phaseName);

    @DefaultValue("false") // Explicitly toggle failure at specific phase - only active if AutoChaos is disabled
    @PropertyName(name = "vms.transformerCycleMonkeyPhaseChaosEnabled.${0}")
    public boolean isTransformerCycleMonkeyPhaseChaosEnabled(String phaseName);

    @DefaultValue("true")
    boolean isProcessRestoreAndInputInParallel();

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

    // A comma-delimited list of top node IDs to drop on the floor prior to processing.
    String getDropTopNodesOnFloor();

    String getOverrideFastlaneIds();

    // The Pin Title Specs
    // Supported FORMAT:
    //    - single title pinned:                                                        version:topNodeId
    //    - multiple title pinned from same blob (ids separated by comman):             version1:topNodeId1,topNodeId2
    //    - multiple title pinned from multiple blobs (specs separated by semicolon):   version1:topNodeId1;version2:topNodeId2
    String getOverridePinTitleSpecs();

    // The VIP of the output data to use when pinning title using output data
    String getOverridePinTitleOutputDataVip();

    @DefaultValue("false")
    boolean useVideoResolutionType();

    @DefaultValue("true")
    boolean isRestoreFromPreviousStateEngine();

    @DefaultValue("true")
    boolean isFailIfRestoreNotAvailable();

    Long getRestoreFromSpecificVersion();

    @DefaultValue("false")
    boolean isCompactionEnabled();

    @DefaultValue("2000000")
    long getCompactionHoleByteThreshold();

    @DefaultValue("10")
    int getCompactionHolePercentThreshold();

    @DefaultValue("false")
    boolean isMerchstillEpisodeLiveCheckEnabled();

    @DefaultValue("true")
    boolean isSeasonNumberForChildrenEnabled();

    //////////////// PUBLISH WORKFLOW ///////////////////

    @DefaultValue("true")
    public boolean isCircuitBreakersEnabled();

    @DefaultValue("false")
    public boolean isHollowBlobDataProviderResetStateEnabled();

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

    @DefaultValue("es_vmsops2")
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

    @DefaultValue("50000")
    int getElasticSearchMaxQueueSize();

    @DefaultValue("6")
    int getElasticSearchMaxTransportThreads();

    @DefaultValue("30000")
    int getElasticSearchQueueTimeoutMillis();

    @DefaultValue("true")
    boolean isElasticSearchNoWaitingEnabled();

    ///////////// TEMPORARY FEATURE-BASED //////////////////
    @DefaultValue("false")
    boolean useCuptokenFeedWithDealIdBasedPrimaryKey();

    ////////////// BUSINESS LOGIC  ////////////////////////

    @DefaultValue("28")
    int getNewContentFlagDuration();

    @DefaultValue("true")
    public boolean isRollupImagesForArtworkScheduling();

    @DefaultValue("true")
    public boolean isFilterImagesForArtworkScheduling();

    @DefaultValue("true")
    public boolean isUseSchedulePhasesInAvailabilityDateCalc();

    @DefaultValue("TITLE_TREATMENT,LOGO_STACKED,LOGO_HORIZONTAL,NETFLIX_ORIGINAL,MOVIE_PERSON_STILL,BB2_OG_LOGO,BB2_OG_LOGO_STACKED,OriginalsPostPlayLogoPostPlay,OriginalsPostPlayLogoPostTrailer,OriginalsPostPlayLogoPrePlay,BB2_OG_LOGO_PLUS,VERTICAL_STORY_ART,MERCH_STILL,MERCH_STILL_4_3,NEW_CONTENT_BADGE,PORTRAIT,SCREENSAVER_BACKGROUND,SCREENSAVER_MIDGROUND,SCREENSAVER_FOREGROUND,NSRE_DATE_BADGE_CROPPED,NSRE_DATE_BADGE,NETFLIX_ORIGINAL_SHADOW_CROPPED,LOGO_HORIZONTAL_GLOW_CROPPED")
    public String getVariableImageTypes();

    @DefaultValue("NEW_EPISODE,NEW_EPISODE_V2,NEW_EPISODE_GLOBAL")
    public String getNewEpisodeOverlayTypes();
    


    ////////////// Multi-language catalog configs ////////////////////////

    @DefaultValue("BE,CH,LU,TH,IL,GR,RO,CY,MD")
    String getMultilanguageCatalogCountries();

    @DefaultValue("true")
    boolean isUseOctoberSkyForMultiLanguageCatalogCountries();

    @DefaultValue("")
    String getOctoberSkyNamespace();

    @DefaultValue("false")
    boolean isGrandfatherEnabled();

    @DefaultValue("true")
    boolean isCountrySpecificLanguageDataMapEnabled();

    @DefaultValue("false")
    boolean isLanguageVariantsForMerchIntentEnabled();


    // Temporary features switching in cinder support

    /**
     * @return true if the VIP and nostreams VIP are produced, published and
     * announced using Cinder and HollowProducer pipelines.
     */
    @DefaultValue("false")
    boolean isCinderEnabled();
}
