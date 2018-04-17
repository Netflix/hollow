package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterQuotePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CharacterQuoteHollow> {

    public CharacterQuotePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CharacterQuotePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CharacterQuote")).getPrimaryKey().getFieldPaths());
    }

    public CharacterQuotePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CharacterQuotePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CharacterQuote", isListenToDataRefresh, fieldPaths);
    }

    public CharacterQuoteHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCharacterQuoteHollow(ordinal);
    }

}