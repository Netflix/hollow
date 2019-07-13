package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ShowSeasonEpisode extends HollowObject {

    public ShowSeasonEpisode(ShowSeasonEpisodeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getDisplaySetId() {
        return delegate().getDisplaySetId(ordinal);
    }

    public Long getDisplaySetIdBoxed() {
        return delegate().getDisplaySetIdBoxed(ordinal);
    }

    public ISOCountryList getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryList(refOrdinal);
    }

    public SeasonList getSeasons() {
        int refOrdinal = delegate().getSeasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSeasonList(refOrdinal);
    }

    public boolean getHideSeasonNumbers() {
        return delegate().getHideSeasonNumbers(ordinal);
    }

    public Boolean getHideSeasonNumbersBoxed() {
        return delegate().getHideSeasonNumbersBoxed(ordinal);
    }

    public boolean getEpisodicNewBadge() {
        return delegate().getEpisodicNewBadge(ordinal);
    }

    public Boolean getEpisodicNewBadgeBoxed() {
        return delegate().getEpisodicNewBadgeBoxed(ordinal);
    }

    public String getMerchOrder() {
        return delegate().getMerchOrder(ordinal);
    }

    public boolean isMerchOrderEqual(String testValue) {
        return delegate().isMerchOrderEqual(ordinal, testValue);
    }

    public HString getMerchOrderHollowReference() {
        int refOrdinal = delegate().getMerchOrderOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public ShowSeasonEpisodeAPI api() {
        return typeApi().getAPI();
    }

    public ShowSeasonEpisodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowSeasonEpisodeDelegate delegate() {
        return (ShowSeasonEpisodeDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code ShowSeasonEpisode} that has a primary key.
     * The primary key is represented by the class {@link ShowSeasonEpisode.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<ShowSeasonEpisode, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, ShowSeasonEpisode.class)
            .bindToPrimaryKey()
            .usingBean(ShowSeasonEpisode.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("displaySetId")
        public final long displaySetId;

        public Key(long movieId, long displaySetId) {
            this.movieId = movieId;
            this.displaySetId = displaySetId;
        }
    }

}