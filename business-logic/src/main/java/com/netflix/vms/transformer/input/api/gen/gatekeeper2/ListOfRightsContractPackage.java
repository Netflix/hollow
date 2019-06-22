package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsContractPackage extends HollowList<RightsContractPackage> {

    public ListOfRightsContractPackage(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsContractPackage instantiateElement(int ordinal) {
        return (RightsContractPackage) api().getRightsContractPackage(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsContractPackageTypeAPI typeApi() {
        return (ListOfRightsContractPackageTypeAPI) delegate.getTypeAPI();
    }

}