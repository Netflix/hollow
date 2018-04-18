package com.netflix.vms.transformer;

import com.netflix.servo.util.VisibleForTesting;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to collect data/messages that are meant for collecting per cycle, per country/language.
 * Useful for logging useful information per country, instead of per title, per country.
 */
public class CycleDataAggregator {

    private static final String DEFAULT_MESSAGE = "No message";
    private static final TaggingLogger.Severity DEFAULT_SEVERITY = TaggingLogger.Severity.INFO;

    private final TransformerContext context;
    private Map<String, Map<TaggingLogger.LogTag, List<Integer>>> cycleDataPerCountry;
    private Map<String, Map<String, Map<TaggingLogger.LogTag, List<Integer>>>> cycleDataPerCountryLanguage;

    private Map<TaggingLogger.LogTag, String> tagMessageConfiguration;
    private Map<TaggingLogger.LogTag, TaggingLogger.Severity> tagSeverityMap;

    public CycleDataAggregator(TransformerContext context) {
        this.context = context;

        this.cycleDataPerCountry = new ConcurrentHashMap<>();
        this.cycleDataPerCountryLanguage = new ConcurrentHashMap<>();

        this.tagMessageConfiguration = new HashMap<>();
        this.tagSeverityMap = new HashMap<>();
    }

    /**
     * Add a message & severity for the log tag. This message is used for representing the aggregated data for this log tag.
     * Configure the aggregator with more information before starting a cycle.
     *
     * @param logTag  LogTag for aggregation
     * @param message Message for the LogTag - A single message to represent aggregated data.
     */
    public void aggregateForLogTag(TaggingLogger.LogTag logTag, TaggingLogger.Severity severity, String message) {
        tagSeverityMap.put(logTag, severity);
        tagMessageConfiguration.put(logTag, message);
    }

    /**
     * Aggregate video ids related to a particular tag aggregated by country. This method is thread-safe.
     *
     * @param country
     * @param videoId
     * @param logTag
     */
    public void collect(String country, int videoId, TransformerLogTag logTag) {

        cycleDataPerCountry.putIfAbsent(country, new ConcurrentHashMap<>());
        Map<TaggingLogger.LogTag, List<Integer>> logTagToIds = cycleDataPerCountry.get(country);

        logTagToIds.putIfAbsent(logTag, Collections.synchronizedList(new ArrayList<>()));
        List<Integer> videoIdList = logTagToIds.get(logTag);
        synchronized (videoIdList) {
            videoIdList.add(videoId);
        }
    }

    /**
     * Aggregate video ids related to a particular tag aggregated by country,language. This method is thread-safe.
     *
     * @param country
     * @param language
     * @param videoId
     * @param logTag
     */
    public void collect(String country, String language, int videoId, TaggingLogger.LogTag logTag) {

        cycleDataPerCountryLanguage.putIfAbsent(country, new ConcurrentHashMap<>());
        Map<String, Map<TaggingLogger.LogTag, List<Integer>>> languageToLogTagMap = cycleDataPerCountryLanguage.get(country);

        languageToLogTagMap.putIfAbsent(language, new ConcurrentHashMap<>());
        Map<TaggingLogger.LogTag, List<Integer>> logTagToListMap = languageToLogTagMap.get(language);

        logTagToListMap.putIfAbsent(logTag, Collections.synchronizedList(new ArrayList<>()));
        List<Integer> videoIdList = logTagToListMap.get(logTag);

        synchronized (videoIdList) {
            videoIdList.add(videoId);
        }
    }

    /**
     * Reset the aggregated data before starting a new cycle.
     */
    public void clearAggregator() {
        cycleDataPerCountry.clear();
        tagMessageConfiguration.clear();
        tagSeverityMap.clear();
    }

    /**
     * Log the aggregated data. Logging logs the aggregated data for each country and log tag.
     */
    public void logAllAggregatedData() {
        TaggingLogger taggingLogger = context.getLogger();
        for (String country : cycleDataPerCountry.keySet()) {
            for (TaggingLogger.LogTag logTag : cycleDataPerCountry.get(country).keySet()) {

                String message = tagMessageConfiguration.get(logTag);
                TaggingLogger.Severity severity = tagSeverityMap.get(logTag);

                if (message == null || message.isEmpty()) message = DEFAULT_MESSAGE;
                if (severity == null) severity = DEFAULT_SEVERITY;

                List<Integer> videoIds = cycleDataPerCountry.get(country).get(logTag);
                if (!videoIds.isEmpty()) {
                    String aggregatedMessage = getJSON(country, null, message, videoIds);
                    taggingLogger.log(severity, Arrays.asList(logTag), aggregatedMessage);
                }
            }
        }

        for (String country : cycleDataPerCountryLanguage.keySet()) {
            for (String lang : cycleDataPerCountryLanguage.get(country).keySet()) {
                for (TaggingLogger.LogTag logTag : cycleDataPerCountryLanguage.get(country).get(lang).keySet()) {

                    String message = tagMessageConfiguration.get(logTag);
                    TaggingLogger.Severity severity = tagSeverityMap.get(logTag);

                    if (message == null || message.isEmpty()) message = DEFAULT_MESSAGE;
                    if (severity == null) severity = DEFAULT_SEVERITY;

                    List<Integer> videoIds = cycleDataPerCountryLanguage.get(country).get(lang).get(logTag);
                    if (!videoIds.isEmpty()) {
                        String aggregatedMessage = getJSON(country, lang, message, videoIds);
                        taggingLogger.log(severity, Arrays.asList(logTag), aggregatedMessage);
                    }
                }
            }
        }
    }

    @VisibleForTesting
    String getJSON(String country, String language, String message, List<Integer> videoIds) {
        StringBuilder builder = new StringBuilder();
        String videoIdsAsString = Arrays.toString(videoIds.toArray());

        builder.append("{");
        builder.append("\"country\":").append("\"" + country + "\"").append(",");
        if (language != null) {
            builder.append("\"language\":").append("\"" + language + "\"").append(",");
        }
        builder.append("\"count\":").append(videoIds.size()).append(",");
        builder.append("\"message\":").append("\"" + message + "\"").append(",");
        builder.append("\"videoIds\":").append(videoIdsAsString);
        builder.append("}");
        return builder.toString();
    }
}
