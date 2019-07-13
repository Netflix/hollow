package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<IPLArtworkDerivative, K> uki = UniqueKeyIndex.from(consumer, IPLArtworkDerivative.class)
 *         .usingBean(k);
 *     IPLArtworkDerivative m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code IPLArtworkDerivative} object.
 */
@Deprecated
@SuppressWarnings("all")
public class IPLArtworkDerivativePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<MceImageV3API, IPLArtworkDerivative> implements HollowUniqueKeyIndex<IPLArtworkDerivative> {

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("IPLArtworkDerivative")).getPrimaryKey().getFieldPaths());
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IPLArtworkDerivative", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public IPLArtworkDerivative findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLArtworkDerivative(ordinal);
    }

}