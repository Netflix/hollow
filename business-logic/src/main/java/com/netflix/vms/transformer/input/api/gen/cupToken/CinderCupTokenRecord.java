package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class CinderCupTokenRecord extends HollowObject {

    public CinderCupTokenRecord(CinderCupTokenRecordDelegate delegate, int ordinal) {
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

    public Long getDealIdBoxed() {
        return delegate().getDealIdBoxed(ordinal);
    }

    public long getDealId() {
        return delegate().getDealId(ordinal);
    }

    public HLong getDealIdHollowReference() {
        int refOrdinal = delegate().getDealIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public String getCupTokenId() {
        return delegate().getCupTokenId(ordinal);
    }

    public boolean isCupTokenIdEqual(String testValue) {
        return delegate().isCupTokenIdEqual(ordinal, testValue);
    }

    public HString getCupTokenIdHollowReference() {
        int refOrdinal = delegate().getCupTokenIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public CupTokenAPI api() {
        return typeApi().getAPI();
    }

    public CinderCupTokenRecordTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CinderCupTokenRecordDelegate delegate() {
        return (CinderCupTokenRecordDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code CinderCupTokenRecord} that has a primary key.
     * The primary key is represented by the class {@link CinderCupTokenRecord.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<CinderCupTokenRecord, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, CinderCupTokenRecord.class)
            .bindToPrimaryKey()
            .usingBean(CinderCupTokenRecord.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("dealId")
        public final long dealId;

        public Key(long movieId, long dealId) {
            this.movieId = movieId;
            this.dealId = dealId;
        }
    }

}