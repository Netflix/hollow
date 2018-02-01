package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AudioStreamInfoPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AudioStreamInfoHollow> {

    public AudioStreamInfoPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public AudioStreamInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("AudioStreamInfo")).getPrimaryKey().getFieldPaths());
    }

    public AudioStreamInfoPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AudioStreamInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AudioStreamInfo", isListenToDataRefresh, fieldPaths);
    }

    public AudioStreamInfoHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAudioStreamInfoHollow(ordinal);
    }

}