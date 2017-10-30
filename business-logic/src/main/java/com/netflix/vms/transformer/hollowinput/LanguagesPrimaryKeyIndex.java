package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LanguagesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, LanguagesHollow> {

    public LanguagesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Languages")).getPrimaryKey().getFieldPaths());
    }

    public LanguagesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LanguagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Languages", isListenToDataRefreah, fieldPaths);
    }

    public LanguagesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLanguagesHollow(ordinal);
    }

}