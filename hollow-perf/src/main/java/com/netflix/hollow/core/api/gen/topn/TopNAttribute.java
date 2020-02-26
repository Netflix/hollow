package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class TopNAttribute extends HollowObject {

    public TopNAttribute(TopNAttributeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getCountry() {
        return delegate().getCountry(ordinal);
    }

    public boolean isCountryEqual(String testValue) {
        return delegate().isCountryEqual(ordinal, testValue);
    }

    public HString getCountryHollowReference() {
        int refOrdinal = delegate().getCountryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public long getCountryViewHoursDaily() {
        return delegate().getCountryViewHoursDaily(ordinal);
    }

    public Long getCountryViewHoursDailyBoxed() {
        return delegate().getCountryViewHoursDailyBoxed(ordinal);
    }

    public long getVideoViewHoursDaily() {
        return delegate().getVideoViewHoursDaily(ordinal);
    }

    public Long getVideoViewHoursDailyBoxed() {
        return delegate().getVideoViewHoursDailyBoxed(ordinal);
    }

    public TopNAPI api() {
        return typeApi().getAPI();
    }

    public TopNAttributeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TopNAttributeDelegate delegate() {
        return (TopNAttributeDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code TopNAttribute} that has a primary key.
     * The primary key is represented by the class {@link String}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<TopNAttribute, String> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, TopNAttribute.class)
            .bindToPrimaryKey()
            .usingPath("country", String.class);
    }

}