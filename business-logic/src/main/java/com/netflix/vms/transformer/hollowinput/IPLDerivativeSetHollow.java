package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class IPLDerivativeSetHollow extends HollowSet<IPLArtworkDerivativeHollow> {

    public IPLDerivativeSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IPLArtworkDerivativeHollow instantiateElement(int ordinal) {
        return (IPLArtworkDerivativeHollow) api().getIPLArtworkDerivativeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeSetTypeAPI typeApi() {
        return (IPLDerivativeSetTypeAPI) delegate.getTypeAPI();
    }

}