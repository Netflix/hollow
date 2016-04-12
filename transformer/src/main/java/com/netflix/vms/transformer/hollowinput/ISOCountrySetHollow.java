package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ISOCountrySetHollow extends HollowSet<ISOCountryHollow> {

    public ISOCountrySetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ISOCountryHollow instantiateElement(int ordinal) {
        return (ISOCountryHollow) api().getISOCountryHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ISOCountrySetTypeAPI typeApi() {
        return (ISOCountrySetTypeAPI) delegate.getTypeAPI();
    }

}