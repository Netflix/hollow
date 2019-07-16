package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieReleaseHistory extends HollowObject {

    public MovieReleaseHistory(MovieReleaseHistoryDelegate delegate, int ordinal) {
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

    public String getType() {
        return delegate().getType(ordinal);
    }

    public boolean isTypeEqual(String testValue) {
        return delegate().isTypeEqual(ordinal, testValue);
    }

    public MovieReleaseType getTypeHollowReference() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieReleaseType(refOrdinal);
    }

    public int getYear() {
        return delegate().getYear(ordinal);
    }

    public Integer getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public int getMonth() {
        return delegate().getMonth(ordinal);
    }

    public Integer getMonthBoxed() {
        return delegate().getMonthBoxed(ordinal);
    }

    public int getDay() {
        return delegate().getDay(ordinal);
    }

    public Integer getDayBoxed() {
        return delegate().getDayBoxed(ordinal);
    }

    public String getDistributorName() {
        return delegate().getDistributorName(ordinal);
    }

    public boolean isDistributorNameEqual(String testValue) {
        return delegate().isDistributorNameEqual(ordinal, testValue);
    }

    public DistributorName getDistributorNameHollowReference() {
        int refOrdinal = delegate().getDistributorNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDistributorName(refOrdinal);
    }

    public String getDistributorBcpCode() {
        return delegate().getDistributorBcpCode(ordinal);
    }

    public boolean isDistributorBcpCodeEqual(String testValue) {
        return delegate().isDistributorBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getDistributorBcpCodeHollowReference() {
        int refOrdinal = delegate().getDistributorBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
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

    public MovieReleaseHistoryTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieReleaseHistoryDelegate delegate() {
        return (MovieReleaseHistoryDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MovieReleaseHistory} that has a primary key.
     * The primary key is represented by the class {@link MovieReleaseHistory.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<MovieReleaseHistory, MovieReleaseHistory.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MovieReleaseHistory.class)
            .bindToPrimaryKey()
            .usingBean(MovieReleaseHistory.Key.class);
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