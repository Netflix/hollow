package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<IndividualSupplemental, K> uki = UniqueKeyIndex.from(consumer, IndividualSupplemental.class)
 *         .usingBean(k);
 *     IndividualSupplemental m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code IndividualSupplemental} object.
 */
@Deprecated
@SuppressWarnings("all")
public class IndividualSupplementalPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<SupplementalAPI, IndividualSupplemental> implements HollowUniqueKeyIndex<IndividualSupplemental> {

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("IndividualSupplemental")).getPrimaryKey().getFieldPaths());
    }

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IndividualSupplemental", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public IndividualSupplemental findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIndividualSupplemental(ordinal);
    }

}