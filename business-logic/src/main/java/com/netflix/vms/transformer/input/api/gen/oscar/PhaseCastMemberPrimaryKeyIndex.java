package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseCastMember, K> uki = UniqueKeyIndex.from(consumer, PhaseCastMember.class)
 *         .usingBean(k);
 *     PhaseCastMember m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseCastMember} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseCastMemberPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseCastMember> implements HollowUniqueKeyIndex<PhaseCastMember> {

    public PhaseCastMemberPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseCastMemberPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseCastMember")).getPrimaryKey().getFieldPaths());
    }

    public PhaseCastMemberPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseCastMemberPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseCastMember", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseCastMember findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseCastMember(ordinal);
    }

}