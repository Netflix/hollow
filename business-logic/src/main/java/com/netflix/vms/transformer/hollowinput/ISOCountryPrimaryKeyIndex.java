package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ISOCountryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ISOCountryHollow> {

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ISOCountry")).getPrimaryKey().getFieldPaths());
    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ISOCountryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ISOCountry", isListenToDataRefresh, fieldPaths);
    }

    public ISOCountryHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getISOCountryHollow(ordinal);
    }

}