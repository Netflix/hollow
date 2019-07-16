package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<SubtypeString, K> uki = UniqueKeyIndex.from(consumer, SubtypeString.class)
 *         .usingBean(k);
 *     SubtypeString m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code SubtypeString} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SubtypeStringPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, SubtypeString> implements HollowUniqueKeyIndex<SubtypeString> {

    public SubtypeStringPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SubtypeStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("SubtypeString")).getPrimaryKey().getFieldPaths());
    }

    public SubtypeStringPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SubtypeStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "SubtypeString", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public SubtypeString findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSubtypeString(ordinal);
    }

}