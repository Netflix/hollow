package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseName, K> uki = UniqueKeyIndex.from(consumer, PhaseName.class)
 *         .usingBean(k);
 *     PhaseName m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseName} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseNamePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseName> implements HollowUniqueKeyIndex<PhaseName> {

    public PhaseNamePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseName")).getPrimaryKey().getFieldPaths());
    }

    public PhaseNamePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseName", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseName findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseName(ordinal);
    }

}