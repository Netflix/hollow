package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AltGenresAlternateNamesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AltGenresAlternateNamesHollow> {

    public AltGenresAlternateNamesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public AltGenresAlternateNamesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("AltGenresAlternateNames")).getPrimaryKey().getFieldPaths());
    }

    public AltGenresAlternateNamesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AltGenresAlternateNamesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "AltGenresAlternateNames", isListenToDataRefreah, fieldPaths);
    }

    public AltGenresAlternateNamesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAltGenresAlternateNamesHollow(ordinal);
    }

}