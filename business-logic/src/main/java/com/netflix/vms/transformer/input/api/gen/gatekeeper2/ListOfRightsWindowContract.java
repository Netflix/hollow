package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsWindowContract extends HollowList<RightsWindowContract> {

    public ListOfRightsWindowContract(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsWindowContract instantiateElement(int ordinal) {
        return (RightsWindowContract) api().getRightsWindowContract(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsWindowContractTypeAPI typeApi() {
        return (ListOfRightsWindowContractTypeAPI) delegate.getTypeAPI();
    }

}