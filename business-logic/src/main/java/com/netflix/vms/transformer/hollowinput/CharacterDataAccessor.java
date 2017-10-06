package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CharacterDataAccessor extends AbstractHollowDataAccessor<CharacterHollow> {

    public static final String TYPE = "CharacterHollow";
    private VMSHollowInputAPI api;

    public CharacterDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CharacterDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CharacterDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CharacterDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CharacterHollow getRecord(int ordinal){
        return api.getCharacterHollow(ordinal);
    }

}