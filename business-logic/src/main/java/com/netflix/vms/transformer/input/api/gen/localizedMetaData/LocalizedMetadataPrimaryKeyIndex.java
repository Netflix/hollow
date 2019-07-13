package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<LocalizedMetadata, K> uki = UniqueKeyIndex.from(consumer, LocalizedMetadata.class)
 *         .usingBean(k);
 *     LocalizedMetadata m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code LocalizedMetadata} object.
 */
@Deprecated
@SuppressWarnings("all")
public class LocalizedMetadataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<LocalizedMetaDataAPI, LocalizedMetadata> implements HollowUniqueKeyIndex<LocalizedMetadata> {

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("LocalizedMetadata")).getPrimaryKey().getFieldPaths());
    }

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "LocalizedMetadata", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public LocalizedMetadata findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLocalizedMetadata(ordinal);
    }

}