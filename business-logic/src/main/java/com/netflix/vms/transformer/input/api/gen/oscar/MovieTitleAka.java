package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitleAka extends HollowObject {

    public MovieTitleAka(MovieTitleAkaDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getId() {
        return delegate().getId(ordinal);
    }

    public Long getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
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

    public String getAlias() {
        return delegate().getAlias(ordinal);
    }

    public boolean isAliasEqual(String testValue) {
        return delegate().isAliasEqual(ordinal, testValue);
    }

    public MovieTitleString getAliasHollowReference() {
        int refOrdinal = delegate().getAliasOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getBcpCode() {
        return delegate().getBcpCode(ordinal);
    }

    public boolean isBcpCodeEqual(String testValue) {
        return delegate().isBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getBcpCodeHollowReference() {
        int refOrdinal = delegate().getBcpCodeOrdinal(ordinal);
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

    public MovieTitleAkaTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieTitleAkaDelegate delegate() {
        return (MovieTitleAkaDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MovieTitleAka} that has a primary key.
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
    public static UniqueKeyIndex<MovieTitleAka, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MovieTitleAka.class)
            .bindToPrimaryKey()
            .usingPath("id", long.class);
    }

}