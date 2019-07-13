package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class FeedMovieCountryLanguages extends HollowObject {

    public FeedMovieCountryLanguages(FeedMovieCountryLanguagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public HLong getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public String getCountryCode() {
        return delegate().getCountryCode(ordinal);
    }

    public boolean isCountryCodeEqual(String testValue) {
        return delegate().isCountryCodeEqual(ordinal, testValue);
    }

    public HString getCountryCodeHollowReference() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public MapOfStringToLong getLanguageToEarliestWindowStartDateMap() {
        int refOrdinal = delegate().getLanguageToEarliestWindowStartDateMapOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfStringToLong(refOrdinal);
    }

    public MclEarliestDateAPI api() {
        return typeApi().getAPI();
    }

    public FeedMovieCountryLanguagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FeedMovieCountryLanguagesDelegate delegate() {
        return (FeedMovieCountryLanguagesDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code FeedMovieCountryLanguages} that has a primary key.
     * The primary key is represented by the class {@link FeedMovieCountryLanguages.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<FeedMovieCountryLanguages, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, FeedMovieCountryLanguages.class)
            .bindToPrimaryKey()
            .usingBean(FeedMovieCountryLanguages.Key.class);
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