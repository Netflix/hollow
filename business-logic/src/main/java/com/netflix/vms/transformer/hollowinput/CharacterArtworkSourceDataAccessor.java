package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CharacterArtworkSourceDataAccessor extends AbstractHollowDataAccessor<CharacterArtworkSourceHollow> {

    public static final String TYPE = "CharacterArtworkSourceHollow";
    private VMSHollowInputAPI api;

    public CharacterArtworkSourceDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CharacterArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CharacterArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CharacterArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CharacterArtworkSourceHollow getRecord(int ordinal){
        return api.getCharacterArtworkSourceHollow(ordinal);
    }

}