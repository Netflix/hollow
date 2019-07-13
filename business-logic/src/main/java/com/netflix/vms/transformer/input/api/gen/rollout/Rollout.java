package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Rollout extends HollowObject {

    public Rollout(RolloutDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getRolloutId() {
        return delegate().getRolloutId(ordinal);
    }

    public Long getRolloutIdBoxed() {
        return delegate().getRolloutIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public String getRolloutName() {
        return delegate().getRolloutName(ordinal);
    }

    public boolean isRolloutNameEqual(String testValue) {
        return delegate().isRolloutNameEqual(ordinal, testValue);
    }

    public HString getRolloutNameHollowReference() {
        int refOrdinal = delegate().getRolloutNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getRolloutType() {
        return delegate().getRolloutType(ordinal);
    }

    public boolean isRolloutTypeEqual(String testValue) {
        return delegate().isRolloutTypeEqual(ordinal, testValue);
    }

    public HString getRolloutTypeHollowReference() {
        int refOrdinal = delegate().getRolloutTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public RolloutPhaseList getPhases() {
        int refOrdinal = delegate().getPhasesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseList(refOrdinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutDelegate delegate() {
        return (RolloutDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code Rollout} that has a primary key.
     * The primary key is represented by the class {@link Rollout.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<Rollout, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, Rollout.class)
            .bindToPrimaryKey()
            .usingBean(Rollout.Key.class);
    }

    public static class Key {
        @FieldPath("rolloutId")
        public final long rolloutId;

        @FieldPath("movieId")
        public final long movieId;

        public Key(long rolloutId, long movieId) {
            this.rolloutId = rolloutId;
            this.movieId = movieId;
        }
    }

}