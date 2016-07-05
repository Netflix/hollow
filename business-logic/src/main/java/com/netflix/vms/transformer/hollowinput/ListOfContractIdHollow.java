package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ListOfContractIdHollow extends HollowList<ContractIdHollow> {

    public ListOfContractIdHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ContractIdHollow instantiateElement(int ordinal) {
        return (ContractIdHollow) api().getContractIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfContractIdTypeAPI typeApi() {
        return (ListOfContractIdTypeAPI) delegate.getTypeAPI();
    }

}