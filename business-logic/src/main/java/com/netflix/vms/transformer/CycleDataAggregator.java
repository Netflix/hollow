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
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

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
    private Map<String, Map<String, Map<TaggingLogger.LogTag, List<JSONMessage>>>> cycleDataPerCountryLanguageJSONMessage;

    private Map<TaggingLogger.LogTag, String> tagMessageConfiguration;
    private Map<TaggingLogger.LogTag, TaggingLogger.Severity> tagSeverityMap;

    public CycleDataAggregator(TransformerContext context) {
        this.context = context;

        this.cycleDataPerCountry = new ConcurrentHashMap<>();
        this.cycleDataPerCountryLanguage = new ConcurrentHashMap<>();
        this.cycleDataPerCountryLanguageJSONMessage = new ConcurrentHashMap<>();

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
     * Aggregate messages for a particular tag aggregated by country, language. This method is thread-safe
     *
     * @param country
     * @param language
     * @param jsonMessage
     * @param logTag
     */
    public void collect(String country, String language, JSONMessage jsonMessage, TaggingLogger.LogTag logTag) {

        cycleDataPerCountryLanguageJSONMessage.putIfAbsent(country, new ConcurrentHashMap<>());
        Map<String, Map<TaggingLogger.LogTag, List<JSONMessage>>> languageToLogTagMap = cycleDataPerCountryLanguageJSONMessage
                .get(country);

        languageToLogTagMap.putIfAbsent(language, new ConcurrentHashMap<>());
        Map<TaggingLogger.LogTag, List<JSONMessage>> logTagToListMap = languageToLogTagMap.get(language);

        logTagToListMap.putIfAbsent(logTag, Collections.synchronizedList(new ArrayList<>()));
        List<JSONMessage> messageList = logTagToListMap.get(logTag);

        synchronized (messageList) {
            messageList.add(jsonMessage);
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
        JsonNodeFactory factory = JsonNodeFactory.instance;
        TaggingLogger taggingLogger = context.getLogger();
        for (String country : cycleDataPerCountry.keySet()) {
            for (TaggingLogger.LogTag logTag : cycleDataPerCountry.get(country).keySet()) {

                String message = tagMessageConfiguration.get(logTag);
                TaggingLogger.Severity severity = tagSeverityMap.get(logTag);

                if (message == null || message.isEmpty()) message = DEFAULT_MESSAGE;
                if (severity == null) severity = DEFAULT_SEVERITY;

                List<Integer> videoIds = cycleDataPerCountry.get(country).get(logTag);
                if (!videoIds.isEmpty()) {
                    String aggregatedMessage = getJSON(factory, country, null, message, videoIds);
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
                        String aggregatedMessage = getJSON(factory, country, lang, message, videoIds);
                        taggingLogger.log(severity, Arrays.asList(logTag), aggregatedMessage);
                    }
                }
            }
        }

        for (String country : cycleDataPerCountryLanguageJSONMessage.keySet()) {
            for (String language : cycleDataPerCountryLanguageJSONMessage.get(country).keySet()) {
                for (TaggingLogger.LogTag logTag : cycleDataPerCountryLanguageJSONMessage.get(country).get(language).keySet()) {

                    String message = tagMessageConfiguration.get(logTag);
                    TaggingLogger.Severity severity = tagSeverityMap.get(logTag);

                    if (message == null || message.isEmpty()) message = DEFAULT_MESSAGE;
                    if (severity == null) severity = DEFAULT_SEVERITY;

                    List<JSONMessage> messages = cycleDataPerCountryLanguageJSONMessage.get(country).get(language).get(logTag);
                    if (!messages.isEmpty()) {
                        String aggregatedMessage = getJSONMessage(factory, country, language, message, messages);
                        taggingLogger.log(severity, Arrays.asList(logTag), aggregatedMessage);
                    }
                }
            }
        }
    }

    String getJSONMessage(JsonNodeFactory factory, String country, String language, String message, List<JSONMessage> messages) {
        ObjectNode jsonObject = getJsonObject(factory, country, language, message, messages.size());
        ArrayNode arrayNode = factory.arrayNode();
        for (JSONMessage jsonMessage : messages)
            arrayNode.add(jsonMessage.getObjectNode());
        jsonObject.put("videoIds", arrayNode);

        return jsonObject.toString();
    }

    @VisibleForTesting
    String getJSON(JsonNodeFactory factory, String country, String language, String message, List<Integer> videoIds) {
        ObjectNode jsonObject = getJsonObject(factory, country, language, message, videoIds.size());

        ArrayNode arrayNode = factory.arrayNode();
        for (int videoId : videoIds)
            arrayNode.add(factory.numberNode(videoId));
        jsonObject.put("videoIds", arrayNode);

        return jsonObject.toString();
    }

    ObjectNode getJsonObject(JsonNodeFactory factory, String country, String language, String message, int count) {
        ObjectNode objectNode = factory.objectNode();
        objectNode.put("country", factory.textNode(country));
        if (language != null)
            objectNode.put("language", factory.textNode(language));
        objectNode.put("count", factory.numberNode(count));
        objectNode.put("message", factory.textNode(message));
        return objectNode;
    }

    public interface JSONMessage {
        ObjectNode getObjectNode();
    }
}