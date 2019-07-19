package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitleNLS extends HollowObject {

    public MovieTitleNLS(MovieTitleNLSDelegate delegate, int ordinal) {
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

    public String getType() {
        return delegate().getType(ordinal);
    }

    public boolean isTypeEqual(String testValue) {
        return delegate().isTypeEqual(ordinal, testValue);
    }

    public MovieTitleType getTypeHollowReference() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleType(refOrdinal);
    }

    public String getTitleText() {
        return delegate().getTitleText(ordinal);
    }

    public boolean isTitleTextEqual(String testValue) {
        return delegate().isTitleTextEqual(ordinal, testValue);
    }

    public MovieTitleString getTitleTextHollowReference() {
        int refOrdinal = delegate().getTitleTextOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getMerchBcpCode() {
        return delegate().getMerchBcpCode(ordinal);
    }

    public boolean isMerchBcpCodeEqual(String testValue) {
        return delegate().isMerchBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getMerchBcpCodeHollowReference() {
        int refOrdinal = delegate().getMerchBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getTitleBcpCode() {
        return delegate().getTitleBcpCode(ordinal);
    }

    public boolean isTitleBcpCodeEqual(String testValue) {
        return delegate().isTitleBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getTitleBcpCodeHollowReference() {
        int refOrdinal = delegate().getTitleBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getSourceType() {
        return delegate().getSourceType(ordinal);
    }

    public boolean isSourceTypeEqual(String testValue) {
        return delegate().isSourceTypeEqual(ordinal, testValue);
    }

    public TitleSourceType getSourceTypeHollowReference() {
        int refOrdinal = delegate().getSourceTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTitleSourceType(refOrdinal);
    }

    public Boolean getIsOriginalTitleBoxed() {
        return delegate().getIsOriginalTitleBoxed(ordinal);
    }

    public boolean getIsOriginalTitle() {
        return delegate().getIsOriginalTitle(ordinal);
    }

    public IsOriginalTitle getIsOriginalTitleHollowReference() {
        int refOrdinal = delegate().getIsOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIsOriginalTitle(refOrdinal);
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

    public MovieTitleNLSTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieTitleNLSDelegate delegate() {
        return (MovieTitleNLSDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MovieTitleNLS} that has a primary key.
     * The primary key is represented by the class {@link MovieTitleNLS.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<MovieTitleNLS, MovieTitleNLS.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MovieTitleNLS.class)
            .bindToPrimaryKey()
            .usingBean(MovieTitleNLS.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("type")
        public final String type;

        @FieldPath("merchBcpCode")
        public final String merchBcpCode;

        public Key(long movieId, String type, String merchBcpCode) {
            this.movieId = movieId;
            this.type = type;
            this.merchBcpCode = merchBcpCode;
        }
    }

}