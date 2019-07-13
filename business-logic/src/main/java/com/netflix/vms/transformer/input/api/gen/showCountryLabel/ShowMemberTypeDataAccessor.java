package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ShowMemberTypeDataAccessor extends AbstractHollowDataAccessor<ShowMemberType> {

    public static final String TYPE = "ShowMemberType";
    private ShowCountryLabelAPI api;

    public ShowMemberTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (ShowCountryLabelAPI)consumer.getAPI();
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine, ShowCountryLabelAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine, ShowCountryLabelAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ShowMemberTypeDataAccessor(HollowReadStateEngine rStateEngine, ShowCountryLabelAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ShowMemberType getRecord(int ordinal){
        return api.getShowMemberType(ordinal);
    }

}