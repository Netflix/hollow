package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<DisallowedAssetBundleEntry, K> uki = UniqueKeyIndex.from(consumer, DisallowedAssetBundleEntry.class)
 *         .usingBean(k);
 *     DisallowedAssetBundleEntry m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code DisallowedAssetBundleEntry} object.
 */
@Deprecated
@SuppressWarnings("all")
public class DisallowedAssetBundleEntryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<ExhibitDealAttributeV1API, DisallowedAssetBundleEntry> implements HollowUniqueKeyIndex<DisallowedAssetBundleEntry> {

    public DisallowedAssetBundleEntryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public DisallowedAssetBundleEntryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("DisallowedAssetBundleEntry")).getPrimaryKey().getFieldPaths());
    }

    public DisallowedAssetBundleEntryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DisallowedAssetBundleEntryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DisallowedAssetBundleEntry", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public DisallowedAssetBundleEntry findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDisallowedAssetBundleEntry(ordinal);
    }

}