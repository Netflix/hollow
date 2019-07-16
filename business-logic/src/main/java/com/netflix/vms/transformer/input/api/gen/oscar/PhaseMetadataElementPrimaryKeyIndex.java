package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseMetadataElement, K> uki = UniqueKeyIndex.from(consumer, PhaseMetadataElement.class)
 *         .usingBean(k);
 *     PhaseMetadataElement m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseMetadataElement} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseMetadataElementPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseMetadataElement> implements HollowUniqueKeyIndex<PhaseMetadataElement> {

    public PhaseMetadataElementPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseMetadataElementPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseMetadataElement")).getPrimaryKey().getFieldPaths());
    }

    public PhaseMetadataElementPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseMetadataElementPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseMetadataElement", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseMetadataElement findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseMetadataElement(ordinal);
    }

}