package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TurboCollectionsDataAccessor extends AbstractHollowDataAccessor<TurboCollectionsHollow> {

    public static final String TYPE = "TurboCollectionsHollow";
    private VMSHollowInputAPI api;

    public TurboCollectionsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TurboCollectionsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TurboCollectionsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TurboCollectionsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TurboCollectionsHollow getRecord(int ordinal){
        return api.getTurboCollectionsHollow(ordinal);
    }

}