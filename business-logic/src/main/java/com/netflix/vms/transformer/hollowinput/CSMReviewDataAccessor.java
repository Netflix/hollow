package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CSMReviewDataAccessor extends AbstractHollowDataAccessor<CSMReviewHollow> {

    public static final String TYPE = "CSMReviewHollow";
    private VMSHollowInputAPI api;

    public CSMReviewDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CSMReviewDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CSMReviewDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CSMReviewDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CSMReviewHollow getRecord(int ordinal){
        return api.getCSMReviewHollow(ordinal);
    }

}