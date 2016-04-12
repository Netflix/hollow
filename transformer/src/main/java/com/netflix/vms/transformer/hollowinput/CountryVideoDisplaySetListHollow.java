package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CountryVideoDisplaySetListHollow extends HollowList<CountryVideoDisplaySetHollow> {

    public CountryVideoDisplaySetListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CountryVideoDisplaySetHollow instantiateElement(int ordinal) {
        return (CountryVideoDisplaySetHollow) api().getCountryVideoDisplaySetHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CountryVideoDisplaySetListTypeAPI typeApi() {
        return (CountryVideoDisplaySetListTypeAPI) delegate.getTypeAPI();
    }

}