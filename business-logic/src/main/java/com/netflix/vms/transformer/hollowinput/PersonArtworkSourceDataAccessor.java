package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonArtworkSourceDataAccessor extends AbstractHollowDataAccessor<PersonArtworkSourceHollow> {

    public static final String TYPE = "PersonArtworkSourceHollow";
    private VMSHollowInputAPI api;

    public PersonArtworkSourceDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonArtworkSourceHollow getRecord(int ordinal){
        return api.getPersonArtworkSourceHollow(ordinal);
    }

}