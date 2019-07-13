package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<DealCountryGroup, K> uki = UniqueKeyIndex.from(consumer, DealCountryGroup.class)
 *         .usingBean(k);
 *     DealCountryGroup m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code DealCountryGroup} object.
 */
@Deprecated
@SuppressWarnings("all")
public class DealCountryGroupPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PackageDealCountryAPI, DealCountryGroup> implements HollowUniqueKeyIndex<DealCountryGroup> {

    public DealCountryGroupPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public DealCountryGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("DealCountryGroup")).getPrimaryKey().getFieldPaths());
    }

    public DealCountryGroupPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DealCountryGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DealCountryGroup", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public DealCountryGroup findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDealCountryGroup(ordinal);
    }

}