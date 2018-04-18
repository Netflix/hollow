package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonArtworkSourcePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PersonArtworkSourceHollow> {

    public PersonArtworkSourcePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PersonArtworkSourcePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PersonArtworkSource")).getPrimaryKey().getFieldPaths());
    }

    public PersonArtworkSourcePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PersonArtworkSourcePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PersonArtworkSource", isListenToDataRefresh, fieldPaths);
    }

    public PersonArtworkSourceHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPersonArtworkSourceHollow(ordinal);
    }

}