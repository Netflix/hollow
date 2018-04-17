package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowCountryLabelPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ShowCountryLabelHollow> {

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ShowCountryLabel")).getPrimaryKey().getFieldPaths());
    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowCountryLabel", isListenToDataRefresh, fieldPaths);
    }

    public ShowCountryLabelHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowCountryLabelHollow(ordinal);
    }

}