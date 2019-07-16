package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SubsDubs extends HollowObject {

    public SubsDubs(SubsDubsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public MovieId getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieId(refOrdinal);
    }

    public String getCountryCode() {
        return delegate().getCountryCode(ordinal);
    }

    public boolean isCountryCodeEqual(String testValue) {
        return delegate().isCountryCodeEqual(ordinal, testValue);
    }

    public CountryString getCountryCodeHollowReference() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCountryString(refOrdinal);
    }

    public boolean getSubsRequired() {
        return delegate().getSubsRequired(ordinal);
    }

    public Boolean getSubsRequiredBoxed() {
        return delegate().getSubsRequiredBoxed(ordinal);
    }

    public boolean getDubsRequired() {
        return delegate().getDubsRequired(ordinal);
    }

    public Boolean getDubsRequiredBoxed() {
        return delegate().getDubsRequiredBoxed(ordinal);
    }

    public Long getDateCreatedBoxed() {
        return delegate().getDateCreatedBoxed(ordinal);
    }

    public long getDateCreated() {
        return delegate().getDateCreated(ordinal);
    }

    public Date getDateCreatedHollowReference() {
        int refOrdinal = delegate().getDateCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public long getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Date getLastUpdatedHollowReference() {
        int refOrdinal = delegate().getLastUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getCreatedBy() {
        return delegate().getCreatedBy(ordinal);
    }

    public boolean isCreatedByEqual(String testValue) {
        return delegate().isCreatedByEqual(ordinal, testValue);
    }

    public HString getCreatedByHollowReference() {
        int refOrdinal = delegate().getCreatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedBy() {
        return delegate().getUpdatedBy(ordinal);
    }

    public boolean isUpdatedByEqual(String testValue) {
        return delegate().isUpdatedByEqual(ordinal, testValue);
    }

    public HString getUpdatedByHollowReference() {
        int refOrdinal = delegate().getUpdatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SubsDubsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SubsDubsDelegate delegate() {
        return (SubsDubsDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code SubsDubs} that has a primary key.
     * The primary key is represented by the class {@link SubsDubs.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<SubsDubs, SubsDubs.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, SubsDubs.class)
            .bindToPrimaryKey()
            .usingBean(SubsDubs.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("countryCode")
        public final String countryCode;

        public Key(long movieId, String countryCode) {
            this.movieId = movieId;
            this.countryCode = countryCode;
        }
    }

}