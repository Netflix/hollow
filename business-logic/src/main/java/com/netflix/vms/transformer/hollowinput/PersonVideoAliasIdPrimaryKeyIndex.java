package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoAliasIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PersonVideoAliasIdHollow> {

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PersonVideoAliasId")).getPrimaryKey().getFieldPaths());
    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonVideoAliasIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonVideoAliasId", isListenToDataRefresh, fieldPaths);
    }

    public PersonVideoAliasIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonVideoAliasIdHollow(ordinal);
    }

}