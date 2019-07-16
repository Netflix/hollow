package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitle extends HollowObject {

    public MovieTitle(MovieTitleDelegate delegate, int ordinal) {
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

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public MovieTitleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieTitleDelegate delegate() {
        return (MovieTitleDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MovieTitle} that has a primary key.
     * The primary key is represented by the class {@link MovieTitle.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<MovieTitle, MovieTitle.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MovieTitle.class)
            .bindToPrimaryKey()
            .usingBean(MovieTitle.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("type")
        public final String type;

        public Key(long movieId, String type) {
            this.movieId = movieId;
            this.type = type;
        }
    }

}