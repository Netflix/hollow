package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<DerivativeTag, K> uki = UniqueKeyIndex.from(consumer, DerivativeTag.class)
 *         .usingBean(k);
 *     DerivativeTag m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code DerivativeTag} object.
 */
@Deprecated
@SuppressWarnings("all")
public class DerivativeTagPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MceImageV3API, DerivativeTag> implements HollowUniqueKeyIndex<DerivativeTag> {

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("DerivativeTag")).getPrimaryKey().getFieldPaths());
    }

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DerivativeTag", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public DerivativeTag findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDerivativeTag(ordinal);
    }

}