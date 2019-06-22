package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Flags, K> uki = UniqueKeyIndex.from(consumer, Flags.class)
 *         .usingBean(k);
 *     Flags m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Flags} object.
 */
@Deprecated
@SuppressWarnings("all")
public class FlagsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, Flags> implements HollowUniqueKeyIndex<Flags> {

    public FlagsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public FlagsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Flags")).getPrimaryKey().getFieldPaths());
    }

    public FlagsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public FlagsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Flags", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Flags findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getFlags(ordinal);
    }

}