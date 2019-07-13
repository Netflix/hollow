package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSet extends HollowObject {

    public IPLArtworkDerivativeSet(IPLArtworkDerivativeSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getDerivativeSetId() {
        return delegate().getDerivativeSetId(ordinal);
    }

    public boolean isDerivativeSetIdEqual(String testValue) {
        return delegate().isDerivativeSetIdEqual(ordinal, testValue);
    }

    public HString getDerivativeSetIdHollowReference() {
        int refOrdinal = delegate().getDerivativeSetIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public IPLDerivativeGroupSet getDerivativesGroupBySource() {
        int refOrdinal = delegate().getDerivativesGroupBySourceOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIPLDerivativeGroupSet(refOrdinal);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public IPLArtworkDerivativeSetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLArtworkDerivativeSetDelegate delegate() {
        return (IPLArtworkDerivativeSetDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code IPLArtworkDerivativeSet} that has a primary key.
     * The primary key is represented by the class {@link String}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<IPLArtworkDerivativeSet, String> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, IPLArtworkDerivativeSet.class)
            .bindToPrimaryKey()
            .usingPath("derivativeSetId", String.class);
    }

}