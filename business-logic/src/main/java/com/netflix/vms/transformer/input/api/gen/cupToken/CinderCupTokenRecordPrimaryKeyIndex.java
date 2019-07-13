package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<CinderCupTokenRecord, K> uki = UniqueKeyIndex.from(consumer, CinderCupTokenRecord.class)
 *         .usingBean(k);
 *     CinderCupTokenRecord m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code CinderCupTokenRecord} object.
 */
@Deprecated
@SuppressWarnings("all")
public class CinderCupTokenRecordPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<CupTokenAPI, CinderCupTokenRecord> implements HollowUniqueKeyIndex<CinderCupTokenRecord> {

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("CinderCupTokenRecord")).getPrimaryKey().getFieldPaths());
    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CinderCupTokenRecord", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public CinderCupTokenRecord findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCinderCupTokenRecord(ordinal);
    }

}