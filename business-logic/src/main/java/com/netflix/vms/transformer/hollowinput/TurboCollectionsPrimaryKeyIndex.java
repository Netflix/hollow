package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TurboCollectionsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TurboCollectionsHollow> {

    public TurboCollectionsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TurboCollections")).getPrimaryKey().getFieldPaths());
    }

    public TurboCollectionsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TurboCollectionsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "TurboCollections", isListenToDataRefreah, fieldPaths);
    }

    public TurboCollectionsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTurboCollectionsHollow(ordinal);
    }

}