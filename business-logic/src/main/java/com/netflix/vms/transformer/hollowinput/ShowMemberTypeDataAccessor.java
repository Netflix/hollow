package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ShowMemberTypeDataAccessor extends AbstractHollowDataAccessor<ShowMemberTypeHollow> {

    public static final String TYPE = "ShowMemberTypeHollow";
    private VMSHollowInputAPI api;

    public ShowMemberTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ShowMemberTypeHollow getRecord(int ordinal){
        return api.getShowMemberTypeHollow(ordinal);
    }

}