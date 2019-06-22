package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Status extends HollowObject {

    public Status(StatusDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
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

    public Rights getRights() {
        int refOrdinal = delegate().getRightsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRights(refOrdinal);
    }

    public Flags getFlags() {
        int refOrdinal = delegate().getFlagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFlags(refOrdinal);
    }

    public AvailableAssets getAvailableAssets() {
        int refOrdinal = delegate().getAvailableAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAvailableAssets(refOrdinal);
    }

    public VideoHierarchyInfo getHierarchyInfo() {
        int refOrdinal = delegate().getHierarchyInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoHierarchyInfo(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public StatusTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StatusDelegate delegate() {
        return (StatusDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code Status} that has a primary key.
     * The primary key is represented by the class {@link Status.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<Status, Status.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, Status.class)
            .bindToPrimaryKey()
            .usingBean(Status.Key.class);
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