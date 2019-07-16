package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieExtension extends HollowObject {

    public MovieExtension(MovieExtensionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieExtensionId() {
        return delegate().getMovieExtensionId(ordinal);
    }

    public Long getMovieExtensionIdBoxed() {
        return delegate().getMovieExtensionIdBoxed(ordinal);
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

    public String getAttributeName() {
        return delegate().getAttributeName(ordinal);
    }

    public boolean isAttributeNameEqual(String testValue) {
        return delegate().isAttributeNameEqual(ordinal, testValue);
    }

    public AttributeName getAttributeNameHollowReference() {
        int refOrdinal = delegate().getAttributeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAttributeName(refOrdinal);
    }

    public String getAttributeValue() {
        return delegate().getAttributeValue(ordinal);
    }

    public boolean isAttributeValueEqual(String testValue) {
        return delegate().isAttributeValueEqual(ordinal, testValue);
    }

    public AttributeValue getAttributeValueHollowReference() {
        int refOrdinal = delegate().getAttributeValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAttributeValue(refOrdinal);
    }

    public SetOfMovieExtensionOverride getOverrides() {
        int refOrdinal = delegate().getOverridesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfMovieExtensionOverride(refOrdinal);
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

    public MovieExtensionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieExtensionDelegate delegate() {
        return (MovieExtensionDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MovieExtension} that has a primary key.
     * The primary key is represented by the class {@link MovieExtension.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<MovieExtension, MovieExtension.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MovieExtension.class)
            .bindToPrimaryKey()
            .usingBean(MovieExtension.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("attributeName")
        public final String attributeName;

        public Key(long movieId, String attributeName) {
            this.movieId = movieId;
            this.attributeName = attributeName;
        }
    }

}