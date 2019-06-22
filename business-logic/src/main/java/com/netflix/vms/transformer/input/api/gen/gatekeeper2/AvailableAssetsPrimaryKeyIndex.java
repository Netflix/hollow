package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<AvailableAssets, K> uki = UniqueKeyIndex.from(consumer, AvailableAssets.class)
 *         .usingBean(k);
 *     AvailableAssets m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code AvailableAssets} object.
 */
@Deprecated
@SuppressWarnings("all")
public class AvailableAssetsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, AvailableAssets> implements HollowUniqueKeyIndex<AvailableAssets> {

    public AvailableAssetsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public AvailableAssetsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("AvailableAssets")).getPrimaryKey().getFieldPaths());
    }

    public AvailableAssetsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AvailableAssetsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AvailableAssets", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public AvailableAssets findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAvailableAssets(ordinal);
    }

}