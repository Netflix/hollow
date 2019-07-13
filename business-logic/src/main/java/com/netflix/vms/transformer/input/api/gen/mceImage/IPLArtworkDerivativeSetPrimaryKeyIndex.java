package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<IPLArtworkDerivativeSet, K> uki = UniqueKeyIndex.from(consumer, IPLArtworkDerivativeSet.class)
 *         .usingBean(k);
 *     IPLArtworkDerivativeSet m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code IPLArtworkDerivativeSet} object.
 */
@Deprecated
@SuppressWarnings("all")
public class IPLArtworkDerivativeSetPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MceImageV3API, IPLArtworkDerivativeSet> implements HollowUniqueKeyIndex<IPLArtworkDerivativeSet> {

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("IPLArtworkDerivativeSet")).getPrimaryKey().getFieldPaths());
    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IPLArtworkDerivativeSet", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public IPLArtworkDerivativeSet findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLArtworkDerivativeSet(ordinal);
    }

}