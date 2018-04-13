package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class IPLDerivativeGroupSetHollow extends HollowSet<IPLDerivativeGroupHollow> {

    public IPLDerivativeGroupSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IPLDerivativeGroupHollow instantiateElement(int ordinal) {
        return (IPLDerivativeGroupHollow) api().getIPLDerivativeGroupHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeGroupSetTypeAPI typeApi() {
        return (IPLDerivativeGroupSetTypeAPI) delegate.getTypeAPI();
    }

}