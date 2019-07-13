package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VmsAttributeFeedEntry, K> uki = UniqueKeyIndex.from(consumer, VmsAttributeFeedEntry.class)
 *         .usingBean(k);
 *     VmsAttributeFeedEntry m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VmsAttributeFeedEntry} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VmsAttributeFeedEntryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<ExhibitDealAttributeV1API, VmsAttributeFeedEntry> implements HollowUniqueKeyIndex<VmsAttributeFeedEntry> {

    public VmsAttributeFeedEntryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VmsAttributeFeedEntryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VmsAttributeFeedEntry")).getPrimaryKey().getFieldPaths());
    }

    public VmsAttributeFeedEntryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VmsAttributeFeedEntryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VmsAttributeFeedEntry", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VmsAttributeFeedEntry findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVmsAttributeFeedEntry(ordinal);
    }

}