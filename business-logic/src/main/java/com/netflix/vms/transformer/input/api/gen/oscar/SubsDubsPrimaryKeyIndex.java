package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<SubsDubs, K> uki = UniqueKeyIndex.from(consumer, SubsDubs.class)
 *         .usingBean(k);
 *     SubsDubs m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code SubsDubs} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SubsDubsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, SubsDubs> implements HollowUniqueKeyIndex<SubsDubs> {

    public SubsDubsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SubsDubsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("SubsDubs")).getPrimaryKey().getFieldPaths());
    }

    public SubsDubsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SubsDubsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "SubsDubs", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public SubsDubs findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSubsDubs(ordinal);
    }

}