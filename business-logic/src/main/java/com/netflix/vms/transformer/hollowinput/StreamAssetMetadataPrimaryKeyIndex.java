package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamAssetMetadataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamAssetMetadataHollow> {

    public StreamAssetMetadataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StreamAssetMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamAssetMetadata")).getPrimaryKey().getFieldPaths());
    }

    public StreamAssetMetadataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamAssetMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StreamAssetMetadata", isListenToDataRefresh, fieldPaths);
    }

    public StreamAssetMetadataHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamAssetMetadataHollow(ordinal);
    }

}