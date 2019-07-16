package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsContractAsset extends HollowList<RightsContractAsset> {

    public ListOfRightsContractAsset(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsContractAsset instantiateElement(int ordinal) {
        return (RightsContractAsset) api().getRightsContractAsset(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsContractAssetTypeAPI typeApi() {
        return (ListOfRightsContractAssetTypeAPI) delegate.getTypeAPI();
    }

}