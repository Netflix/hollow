package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowCountryLabel extends HollowObject {

    public ShowCountryLabel(ShowCountryLabelDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public ShowMemberTypeList getShowMemberTypes() {
        int refOrdinal = delegate().getShowMemberTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getShowMemberTypeList(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public ShowCountryLabelTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowCountryLabelDelegate delegate() {
        return (ShowCountryLabelDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code ShowCountryLabel} that has a primary key.
     * The primary key is represented by the type {@code long}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<ShowCountryLabel, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, ShowCountryLabel.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}