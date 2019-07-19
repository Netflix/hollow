package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ShowCountryLabel, K> uki = UniqueKeyIndex.from(consumer, ShowCountryLabel.class)
 *         .usingBean(k);
 *     ShowCountryLabel m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ShowCountryLabel} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ShowCountryLabelPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, ShowCountryLabel> implements HollowUniqueKeyIndex<ShowCountryLabel> {

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ShowCountryLabel")).getPrimaryKey().getFieldPaths());
    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowCountryLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowCountryLabel", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ShowCountryLabel findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowCountryLabel(ordinal);
    }

}