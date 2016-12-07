package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ISOCountrySetHollow extends HollowSet<ISOCountryHollow> {

    public ISOCountrySetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ISOCountryHollow instantiateElement(int ordinal) {
        return (ISOCountryHollow) api().getISOCountryHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ISOCountrySetTypeAPI typeApi() {
        return (ISOCountrySetTypeAPI) delegate.getTypeAPI();
    }

}