package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<BcpCode, K> uki = UniqueKeyIndex.from(consumer, BcpCode.class)
 *         .usingBean(k);
 *     BcpCode m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code BcpCode} object.
 */
@Deprecated
@SuppressWarnings("all")
public class BcpCodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, BcpCode> implements HollowUniqueKeyIndex<BcpCode> {

    public BcpCodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public BcpCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("BcpCode")).getPrimaryKey().getFieldPaths());
    }

    public BcpCodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public BcpCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "BcpCode", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public BcpCode findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getBcpCode(ordinal);
    }

}