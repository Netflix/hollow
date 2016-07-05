package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ListOfRightsContractHollow extends HollowList<RightsContractHollow> {

    public ListOfRightsContractHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RightsContractHollow instantiateElement(int ordinal) {
        return (RightsContractHollow) api().getRightsContractHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsContractTypeAPI typeApi() {
        return (ListOfRightsContractTypeAPI) delegate.getTypeAPI();
    }

}