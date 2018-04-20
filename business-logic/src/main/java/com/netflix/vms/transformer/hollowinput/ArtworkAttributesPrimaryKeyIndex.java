package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkAttributesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ArtworkAttributesHollow> {

    public ArtworkAttributesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ArtworkAttributesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ArtworkAttributes")).getPrimaryKey().getFieldPaths());
    }

    public ArtworkAttributesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ArtworkAttributesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ArtworkAttributes", isListenToDataRefresh, fieldPaths);
    }

    public ArtworkAttributesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getArtworkAttributesHollow(ordinal);
    }

}