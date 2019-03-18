package com.netflix.vms.transformer.publish.workflow.logmessage;

import com.netflix.vms.transformer.common.MessageBuilder;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import java.util.Collection;
import java.util.List;

public class ViewShareMessage {
    private final MessageBuilder msgBuilder;

    public ViewShareMessage(String type, String countryId, Collection<Integer> videoIds, Float missingViewShare, Float threshold) {
        msgBuilder = new MessageBuilder(type + " circuitbreaker ");
        build(countryId, getVideoIdsAsString(videoIds), missingViewShare, threshold);
    }

    public ViewShareMessage(String type, String countryId, List<VideoCountryKey> failedIDs, Float missingViewShare, Float threshold) {
        msgBuilder = new MessageBuilder(type + " circuitbreaker ");
        build(countryId, getVideoIdsAsString(failedIDs, countryId), missingViewShare, threshold);
    }

    private void build(String countryId, String videoIds, Float missingViewShare, Float threshold) {
        msgBuilder.put("countryId", countryId);
        if (videoIds != null)
            msgBuilder.put("videoIds", videoIds);
        if (missingViewShare != null)
            msgBuilder.put("missingViewShare", missingViewShare);
        if (threshold != null)
            msgBuilder.put("threshold", threshold);
    }

    private String getVideoIdsAsString(Collection<Integer> videoIds) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        int index = 0;
        for (Integer id : videoIds) {
            if (index != 0)
                buffer.append(", ");
            buffer.append(id);
            index++;
        }
        buffer.append(']');
        return buffer.toString();
    }

    private String getVideoIdsAsString(List<VideoCountryKey> videoIds, String country) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        int index = 0;
        for (VideoCountryKey videoCountry : videoIds) {
            if (country.equals(videoCountry.getCountry())) {
                if (index++ != 0) buffer.append(", ");
                buffer.append(videoCountry.getVideoId());
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    @Override
    public String toString() {
        return msgBuilder.toString();
    }
}
