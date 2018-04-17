package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharactersPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CharactersHollow> {

    public CharactersPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Characters")).getPrimaryKey().getFieldPaths());
    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CharactersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Characters", isListenToDataRefresh, fieldPaths);
    }

    public CharactersHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCharactersHollow(ordinal);
    }

}