package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonCharacterResourcePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PersonCharacterResourceHollow> {

    public PersonCharacterResourcePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PersonCharacterResourcePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PersonCharacterResource")).getPrimaryKey().getFieldPaths());
    }

    public PersonCharacterResourcePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonCharacterResourcePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonCharacterResource", isListenToDataRefresh, fieldPaths);
    }

    public PersonCharacterResourceHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonCharacterResourceHollow(ordinal);
    }

}