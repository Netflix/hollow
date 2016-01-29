package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TerritoryCountriesArrayOfCountryCodesHollow extends HollowList<TerritoryCountriesCountryCodesHollow> {

    public TerritoryCountriesArrayOfCountryCodesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TerritoryCountriesCountryCodesHollow instantiateElement(int ordinal) {
        return (TerritoryCountriesCountryCodesHollow) api().getTerritoryCountriesCountryCodesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TerritoryCountriesArrayOfCountryCodesTypeAPI typeApi() {
        return (TerritoryCountriesArrayOfCountryCodesTypeAPI) delegate.getTypeAPI();
    }

}