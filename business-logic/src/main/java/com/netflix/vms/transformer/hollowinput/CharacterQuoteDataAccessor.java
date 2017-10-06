package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CharacterQuoteDataAccessor extends AbstractHollowDataAccessor<CharacterQuoteHollow> {

    public static final String TYPE = "CharacterQuoteHollow";
    private VMSHollowInputAPI api;

    public CharacterQuoteDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CharacterQuoteDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CharacterQuoteDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CharacterQuoteDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CharacterQuoteHollow getRecord(int ordinal){
        return api.getCharacterQuoteHollow(ordinal);
    }

}