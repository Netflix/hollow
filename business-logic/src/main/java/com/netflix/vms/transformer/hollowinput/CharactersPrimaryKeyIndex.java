package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharactersPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CharactersHollow> {

    public CharactersPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Characters")).getPrimaryKey().getFieldPaths());
    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Characters", isListenToDataRefreah, fieldPaths);
    }

    public CharactersHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCharactersHollow(ordinal);
    }

}