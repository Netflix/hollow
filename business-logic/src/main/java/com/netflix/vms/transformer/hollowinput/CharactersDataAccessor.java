package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CharactersDataAccessor extends AbstractHollowDataAccessor<CharactersHollow> {

    public static final String TYPE = "CharactersHollow";
    private VMSHollowInputAPI api;

    public CharactersDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CharactersDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CharactersDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CharactersDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CharactersHollow getRecord(int ordinal){
        return api.getCharactersHollow(ordinal);
    }

}