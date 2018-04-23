package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CharacterPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CharacterHollow> {

    public CharacterPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CharacterPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Character")).getPrimaryKey().getFieldPaths());
    }

    public CharacterPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CharacterPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Character", isListenToDataRefresh, fieldPaths);
    }

    public CharacterHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCharacterHollow(ordinal);
    }

}