package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LocalizedCharacterPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, LocalizedCharacterHollow> {

    public LocalizedCharacterPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public LocalizedCharacterPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("LocalizedCharacter")).getPrimaryKey().getFieldPaths());
    }

    public LocalizedCharacterPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LocalizedCharacterPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "LocalizedCharacter", isListenToDataRefreah, fieldPaths);
    }

    public LocalizedCharacterHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLocalizedCharacterHollow(ordinal);
    }

}