package com.netflix.vms.transformer.fastlane;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.mutationstream.fastlane.FastlaneCassandraHelper;
import com.netflix.mutationstream.fastlane.FastlaneVideo;
import com.netflix.vms.transformer.common.config.TransformerConfig;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class FastlaneIdRetriever {

    private final TransformerConfig config;
    private final FastlaneCassandraHelper fastlaneCassandraHelper;

    @Inject
    public FastlaneIdRetriever(TransformerConfig config, FastlaneCassandraHelper cassandraHelper) {
        this.config = config;
        this.fastlaneCassandraHelper = cassandraHelper;
    }

    public Set<Integer> getFastlaneIds() {
        if(config.getOverrideFastlaneIds() != null)
            return configuredOverrideFastlaneIds();
        else
            return fastlaneIdsFromCassandra();
    }

    public Set<String> getTitleOverrideSpecs() {
        Set<String> specs = new HashSet<>();
        for (String spec : config.getOverrideTitleSpecs().split(",")) {
            specs.add(spec);
        }
        return specs;

    }

    private Set<Integer> fastlaneIdsFromCassandra() {
        Set<Integer> ids = new HashSet<>();
        try {
            long now = System.currentTimeMillis();

            for(FastlaneVideo vid : fastlaneCassandraHelper.getFastlaneVideos()) {
                if(vid.getStartWindow().getTime() < now && now < vid.getEndWindow().getTime())
                    ids.add(vid.getVideoId());
            }

            return ids;
        } catch(ConnectionException ex) {
            throw new RuntimeException("Unable to retrieve FastLane IDs", ex);
        }
    }

    private Set<Integer> configuredOverrideFastlaneIds() {
        Set<Integer> ids = new HashSet<>();
        for(String idStr : config.getOverrideFastlaneIds().split(",")) {
            ids.add(Integer.parseInt(idStr));
        }
        return ids;
    }

}
