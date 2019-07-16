package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

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

    public String getRolloutName() {
        return delegate().getRolloutName(ordinal);
    }

    public boolean isRolloutNameEqual(String testValue) {
        return delegate().isRolloutNameEqual(ordinal, testValue);
    }

    public RolloutName getRolloutNameHollowReference() {
        int refOrdinal = delegate().getRolloutNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutName(refOrdinal);
    }

    public String getType() {
        return delegate().getType(ordinal);
    }

    public boolean isTypeEqual(String testValue) {
        return delegate().isTypeEqual(ordinal, testValue);
    }

    public RolloutType getTypeHollowReference() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutType(refOrdinal);
    }

    public String getStatus() {
        return delegate().getStatus(ordinal);
    }

    public boolean isStatusEqual(String testValue) {
        return delegate().isStatusEqual(ordinal, testValue);
    }

    public RolloutStatus getStatusHollowReference() {
        int refOrdinal = delegate().getStatusOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutStatus(refOrdinal);
    }

    public SetOfRolloutPhase getPhases() {
        int refOrdinal = delegate().getPhasesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfRolloutPhase(refOrdinal);
    }

    public SetOfRolloutCountry getCountries() {
        int refOrdinal = delegate().getCountriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfRolloutCountry(refOrdinal);
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

    public RolloutTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutDelegate delegate() {
        return (RolloutDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code Rollout} that has a primary key.
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
    public static UniqueKeyIndex<Rollout, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, Rollout.class)
            .bindToPrimaryKey()
            .usingPath("rolloutId", long.class);
    }

}