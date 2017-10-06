package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AudioStreamInfoDataAccessor extends AbstractHollowDataAccessor<AudioStreamInfoHollow> {

    public static final String TYPE = "AudioStreamInfoHollow";
    private VMSHollowInputAPI api;

    public AudioStreamInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AudioStreamInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AudioStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AudioStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AudioStreamInfoHollow getRecord(int ordinal){
        return api.getAudioStreamInfoHollow(ordinal);
    }

}