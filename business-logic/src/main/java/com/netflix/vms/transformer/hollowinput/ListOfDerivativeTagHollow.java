package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfDerivativeTagHollow extends HollowList<DerivativeTagHollow> {

    public ListOfDerivativeTagHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DerivativeTagHollow instantiateElement(int ordinal) {
        return (DerivativeTagHollow) api().getDerivativeTagHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfDerivativeTagTypeAPI typeApi() {
        return (ListOfDerivativeTagTypeAPI) delegate.getTypeAPI();
    }

}