package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoRolePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PersonVideoRoleHollow> {

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PersonVideoRole")).getPrimaryKey().getFieldPaths());
    }

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonVideoRolePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "PersonVideoRole", isListenToDataRefreah, fieldPaths);
    }

    public PersonVideoRoleHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonVideoRoleHollow(ordinal);
    }

}