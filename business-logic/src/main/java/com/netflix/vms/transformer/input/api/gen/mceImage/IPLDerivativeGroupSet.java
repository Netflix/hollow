package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class IPLDerivativeGroupSet extends HollowSet<IPLDerivativeGroup> {

    public IPLDerivativeGroupSet(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IPLDerivativeGroup instantiateElement(int ordinal) {
        return (IPLDerivativeGroup) api().getIPLDerivativeGroup(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeGroupSetTypeAPI typeApi() {
        return (IPLDerivativeGroupSetTypeAPI) delegate.getTypeAPI();
    }

}