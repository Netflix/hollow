package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonBioPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PersonBioHollow> {

    public PersonBioPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PersonBioPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PersonBio")).getPrimaryKey().getFieldPaths());
    }

    public PersonBioPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonBioPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonBio", isListenToDataRefresh, fieldPaths);
    }

    public PersonBioHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonBioHollow(ordinal);
    }

}