package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetHollow extends HollowObject {

    public IPLArtworkDerivativeSetHollow(IPLArtworkDerivativeSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getDerivativeSetId() {
        int refOrdinal = delegate().getDerivativeSetIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public IPLDerivativeGroupSetHollow _getDerivativesGroupBySource() {
        int refOrdinal = delegate().getDerivativesGroupBySourceOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIPLDerivativeGroupSetHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IPLArtworkDerivativeSetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLArtworkDerivativeSetDelegate delegate() {
        return (IPLArtworkDerivativeSetDelegate)delegate;
    }

}