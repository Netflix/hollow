package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsWindowContractHollow extends HollowList<RightsWindowContractHollow> {

    public ListOfRightsWindowContractHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsWindowContractHollow instantiateElement(int ordinal) {
        return (RightsWindowContractHollow) api().getRightsWindowContractHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsWindowContractTypeAPI typeApi() {
        return (ListOfRightsWindowContractTypeAPI) delegate.getTypeAPI();
    }

}