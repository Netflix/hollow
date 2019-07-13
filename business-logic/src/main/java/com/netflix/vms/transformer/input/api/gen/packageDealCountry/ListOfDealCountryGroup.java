package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfDealCountryGroup extends HollowList<DealCountryGroup> {

    public ListOfDealCountryGroup(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DealCountryGroup instantiateElement(int ordinal) {
        return (DealCountryGroup) api().getDealCountryGroup(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public ListOfDealCountryGroupTypeAPI typeApi() {
        return (ListOfDealCountryGroupTypeAPI) delegate.getTypeAPI();
    }

}