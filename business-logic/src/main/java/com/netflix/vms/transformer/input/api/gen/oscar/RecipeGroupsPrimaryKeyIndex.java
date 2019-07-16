package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RecipeGroups, K> uki = UniqueKeyIndex.from(consumer, RecipeGroups.class)
 *         .usingBean(k);
 *     RecipeGroups m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RecipeGroups} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RecipeGroupsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RecipeGroups> implements HollowUniqueKeyIndex<RecipeGroups> {

    public RecipeGroupsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RecipeGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RecipeGroups")).getPrimaryKey().getFieldPaths());
    }

    public RecipeGroupsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RecipeGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RecipeGroups", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RecipeGroups findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRecipeGroups(ordinal);
    }

}