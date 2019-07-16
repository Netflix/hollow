package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<SourceRequestDefaultFulfillment, K> uki = UniqueKeyIndex.from(consumer, SourceRequestDefaultFulfillment.class)
 *         .usingBean(k);
 *     SourceRequestDefaultFulfillment m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code SourceRequestDefaultFulfillment} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SourceRequestDefaultFulfillmentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, SourceRequestDefaultFulfillment> implements HollowUniqueKeyIndex<SourceRequestDefaultFulfillment> {

    public SourceRequestDefaultFulfillmentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SourceRequestDefaultFulfillmentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("SourceRequestDefaultFulfillment")).getPrimaryKey().getFieldPaths());
    }

    public SourceRequestDefaultFulfillmentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SourceRequestDefaultFulfillmentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "SourceRequestDefaultFulfillment", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public SourceRequestDefaultFulfillment findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSourceRequestDefaultFulfillment(ordinal);
    }

}