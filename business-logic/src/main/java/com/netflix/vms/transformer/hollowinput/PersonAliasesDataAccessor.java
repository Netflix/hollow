package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonAliasesDataAccessor extends AbstractHollowDataAccessor<PersonAliasesHollow> {

    public static final String TYPE = "PersonAliasesHollow";
    private VMSHollowInputAPI api;

    public PersonAliasesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonAliasesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonAliasesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonAliasesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonAliasesHollow getRecord(int ordinal){
        return api.getPersonAliasesHollow(ordinal);
    }

}