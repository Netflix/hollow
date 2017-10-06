package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CharacterElementsDataAccessor extends AbstractHollowDataAccessor<CharacterElementsHollow> {

    public static final String TYPE = "CharacterElementsHollow";
    private VMSHollowInputAPI api;

    public CharacterElementsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CharacterElementsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CharacterElementsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CharacterElementsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CharacterElementsHollow getRecord(int ordinal){
        return api.getCharacterElementsHollow(ordinal);
    }

}