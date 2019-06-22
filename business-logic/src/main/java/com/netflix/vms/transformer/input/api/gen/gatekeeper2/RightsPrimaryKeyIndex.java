package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Rights, K> uki = UniqueKeyIndex.from(consumer, Rights.class)
 *         .usingBean(k);
 *     Rights m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Rights} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RightsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, Rights> implements HollowUniqueKeyIndex<Rights> {

    public RightsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RightsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Rights")).getPrimaryKey().getFieldPaths());
    }

    public RightsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Rights", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Rights findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRights(ordinal);
    }

}