package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LocalizedMetadataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, LocalizedMetadataHollow> {

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("LocalizedMetadata")).getPrimaryKey().getFieldPaths());
    }

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "LocalizedMetadata", isListenToDataRefreah, fieldPaths);
    }

    public LocalizedMetadataHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLocalizedMetadataHollow(ordinal);
    }

}