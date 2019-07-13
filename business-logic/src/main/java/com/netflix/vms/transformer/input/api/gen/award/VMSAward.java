package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VMSAward extends HollowObject {

    public VMSAward(VMSAwardDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getAwardId() {
        return delegate().getAwardId(ordinal);
    }

    public Long getAwardIdBoxed() {
        return delegate().getAwardIdBoxed(ordinal);
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

    public long getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public boolean getIsMovieAward() {
        return delegate().getIsMovieAward(ordinal);
    }

    public Boolean getIsMovieAwardBoxed() {
        return delegate().getIsMovieAwardBoxed(ordinal);
    }

    public long getFestivalId() {
        return delegate().getFestivalId(ordinal);
    }

    public Long getFestivalIdBoxed() {
        return delegate().getFestivalIdBoxed(ordinal);
    }

    public AwardAPI api() {
        return typeApi().getAPI();
    }

    public VMSAwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VMSAwardDelegate delegate() {
        return (VMSAwardDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VMSAward} that has a primary key.
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
    public static UniqueKeyIndex<VMSAward, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VMSAward.class)
            .bindToPrimaryKey()
            .usingPath("awardId", long.class);
    }

}