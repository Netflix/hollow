package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DisallowedSubtitleLangCodeDataAccessor extends AbstractHollowDataAccessor<DisallowedSubtitleLangCodeHollow> {

    public static final String TYPE = "DisallowedSubtitleLangCodeHollow";
    private VMSHollowInputAPI api;

    public DisallowedSubtitleLangCodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DisallowedSubtitleLangCodeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DisallowedSubtitleLangCodeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DisallowedSubtitleLangCodeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DisallowedSubtitleLangCodeHollow getRecord(int ordinal){
        return api.getDisallowedSubtitleLangCodeHollow(ordinal);
    }

}