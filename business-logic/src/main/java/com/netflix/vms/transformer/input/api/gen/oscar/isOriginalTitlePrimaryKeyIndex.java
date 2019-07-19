package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<IsOriginalTitle, K> uki = UniqueKeyIndex.from(consumer, IsOriginalTitle.class)
 *         .usingBean(k);
 *     IsOriginalTitle m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code IsOriginalTitle} object.
 */
@Deprecated
@SuppressWarnings("all")
public class isOriginalTitlePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, IsOriginalTitle> implements HollowUniqueKeyIndex<IsOriginalTitle> {

    public isOriginalTitlePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public isOriginalTitlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("isOriginalTitle")).getPrimaryKey().getFieldPaths());
    }

    public isOriginalTitlePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public isOriginalTitlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "isOriginalTitle", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public IsOriginalTitle findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIsOriginalTitle(ordinal);
    }

}