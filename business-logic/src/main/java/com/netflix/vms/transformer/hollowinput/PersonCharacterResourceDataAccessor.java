package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonCharacterResourceDataAccessor extends AbstractHollowDataAccessor<PersonCharacterResourceHollow> {

    public static final String TYPE = "PersonCharacterResourceHollow";
    private VMSHollowInputAPI api;

    public PersonCharacterResourceDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonCharacterResourceDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonCharacterResourceDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonCharacterResourceDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonCharacterResourceHollow getRecord(int ordinal){
        return api.getPersonCharacterResourceHollow(ordinal);
    }

}