package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class LocalizedMetadata extends HollowObject {

    public LocalizedMetadata(LocalizedMetadataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public String getAttributeName() {
        return delegate().getAttributeName(ordinal);
    }

    public boolean isAttributeNameEqual(String testValue) {
        return delegate().isAttributeNameEqual(ordinal, testValue);
    }

    public HString getAttributeNameHollowReference() {
        int refOrdinal = delegate().getAttributeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getLabel() {
        return delegate().getLabel(ordinal);
    }

    public boolean isLabelEqual(String testValue) {
        return delegate().isLabelEqual(ordinal, testValue);
    }

    public HString getLabelHollowReference() {
        int refOrdinal = delegate().getLabelOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public MapOfTranslatedText getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfTranslatedText(refOrdinal);
    }

    public LocalizedMetaDataAPI api() {
        return typeApi().getAPI();
    }

    public LocalizedMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LocalizedMetadataDelegate delegate() {
        return (LocalizedMetadataDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code LocalizedMetadata} that has a primary key.
     * The primary key is represented by the class {@link LocalizedMetadata.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<LocalizedMetadata, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, LocalizedMetadata.class)
            .bindToPrimaryKey()
            .usingBean(LocalizedMetadata.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("attributeName")
        public final String attributeName;

        @FieldPath("label")
        public final String label;

        public Key(long movieId, String attributeName, String label) {
            this.movieId = movieId;
            this.attributeName = attributeName;
            this.label = label;
        }
    }

}