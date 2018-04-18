package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsContractAssetHollow extends HollowList<RightsContractAssetHollow> {

    public ListOfRightsContractAssetHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsContractAssetHollow instantiateElement(int ordinal) {
        return (RightsContractAssetHollow) api().getRightsContractAssetHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsContractAssetTypeAPI typeApi() {
        return (ListOfRightsContractAssetTypeAPI) delegate.getTypeAPI();
    }

}