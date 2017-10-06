package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonCharacterDataAccessor extends AbstractHollowDataAccessor<PersonCharacterHollow> {

    public static final String TYPE = "PersonCharacterHollow";
    private VMSHollowInputAPI api;

    public PersonCharacterDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonCharacterDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonCharacterDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonCharacterDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonCharacterHollow getRecord(int ordinal){
        return api.getPersonCharacterHollow(ordinal);
    }

}