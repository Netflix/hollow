package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsContractPackageHollow extends HollowList<RightsContractPackageHollow> {

    public ListOfRightsContractPackageHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsContractPackageHollow instantiateElement(int ordinal) {
        return (RightsContractPackageHollow) api().getRightsContractPackageHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsContractPackageTypeAPI typeApi() {
        return (ListOfRightsContractPackageTypeAPI) delegate.getTypeAPI();
    }

}