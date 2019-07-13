package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Supplementals extends HollowObject {

    public Supplementals(SupplementalsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public SupplementalsList getSupplementals() {
        int refOrdinal = delegate().getSupplementalsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSupplementalsList(refOrdinal);
    }

    public SupplementalAPI api() {
        return typeApi().getAPI();
    }

    public SupplementalsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SupplementalsDelegate delegate() {
        return (SupplementalsDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code Supplementals} that has a primary key.
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
    public static UniqueKeyIndex<Supplementals, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, Supplementals.class)
            .bindToPrimaryKey()
            .usingPath("movieId", long.class);
    }

}