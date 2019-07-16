package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ShowCountryLabelOverride, K> uki = UniqueKeyIndex.from(consumer, ShowCountryLabelOverride.class)
 *         .usingBean(k);
 *     ShowCountryLabelOverride m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ShowCountryLabelOverride} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ShowCountryLabelOverridePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, ShowCountryLabelOverride> implements HollowUniqueKeyIndex<ShowCountryLabelOverride> {

    public ShowCountryLabelOverridePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ShowCountryLabelOverridePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ShowCountryLabelOverride")).getPrimaryKey().getFieldPaths());
    }

    public ShowCountryLabelOverridePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowCountryLabelOverridePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowCountryLabelOverride", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ShowCountryLabelOverride findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowCountryLabelOverride(ordinal);
    }

}