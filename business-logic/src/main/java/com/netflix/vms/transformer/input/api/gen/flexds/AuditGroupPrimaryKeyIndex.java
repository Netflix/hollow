package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<AuditGroup, K> uki = UniqueKeyIndex.from(consumer, AuditGroup.class)
 *         .usingBean(k);
 *     AuditGroup m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code AuditGroup} object.
 */
@Deprecated
@SuppressWarnings("all")
public class AuditGroupPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<FlexDSAPI, AuditGroup> implements HollowUniqueKeyIndex<AuditGroup> {

    public AuditGroupPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public AuditGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("AuditGroup")).getPrimaryKey().getFieldPaths());
    }

    public AuditGroupPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AuditGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AuditGroup", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public AuditGroup findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAuditGroup(ordinal);
    }

}