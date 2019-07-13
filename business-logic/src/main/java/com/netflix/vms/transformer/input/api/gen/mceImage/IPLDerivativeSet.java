package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class IPLDerivativeSet extends HollowSet<IPLArtworkDerivative> {

    public IPLDerivativeSet(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IPLArtworkDerivative instantiateElement(int ordinal) {
        return (IPLArtworkDerivative) api().getIPLArtworkDerivative(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeSetTypeAPI typeApi() {
        return (IPLDerivativeSetTypeAPI) delegate.getTypeAPI();
    }

}