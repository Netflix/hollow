package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TitleSetupRequirementsTemplate, K> uki = UniqueKeyIndex.from(consumer, TitleSetupRequirementsTemplate.class)
 *         .usingBean(k);
 *     TitleSetupRequirementsTemplate m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TitleSetupRequirementsTemplate} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TitleSetupRequirementsTemplatePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, TitleSetupRequirementsTemplate> implements HollowUniqueKeyIndex<TitleSetupRequirementsTemplate> {

    public TitleSetupRequirementsTemplatePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TitleSetupRequirementsTemplatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TitleSetupRequirementsTemplate")).getPrimaryKey().getFieldPaths());
    }

    public TitleSetupRequirementsTemplatePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TitleSetupRequirementsTemplatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TitleSetupRequirementsTemplate", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TitleSetupRequirementsTemplate findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTitleSetupRequirementsTemplate(ordinal);
    }

}