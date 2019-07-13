package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DealCountryGroup extends HollowObject {

    public DealCountryGroup(DealCountryGroupDelegate delegate, int ordinal) {
        super(delegate, ordinal);
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

    public MapOfStringToBoolean getCountryWindow() {
        int refOrdinal = delegate().getCountryWindowOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfStringToBoolean(refOrdinal);
    }

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public DealCountryGroupTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DealCountryGroupDelegate delegate() {
        return (DealCountryGroupDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code DealCountryGroup} that has a primary key.
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
    public static UniqueKeyIndex<DealCountryGroup, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, DealCountryGroup.class)
            .bindToPrimaryKey()
            .usingPath("dealId", long.class);
    }

}