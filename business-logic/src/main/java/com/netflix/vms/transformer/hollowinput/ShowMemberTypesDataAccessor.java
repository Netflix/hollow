package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ShowMemberTypesDataAccessor extends AbstractHollowDataAccessor<ShowMemberTypesHollow> {

    public static final String TYPE = "ShowMemberTypesHollow";
    private VMSHollowInputAPI api;

    public ShowMemberTypesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ShowMemberTypesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ShowMemberTypesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ShowMemberTypesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ShowMemberTypesHollow getRecord(int ordinal){
        return api.getShowMemberTypesHollow(ordinal);
    }

}