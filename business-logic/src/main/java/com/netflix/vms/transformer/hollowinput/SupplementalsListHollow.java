package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SupplementalsListHollow extends HollowList<IndividualSupplementalHollow> {

    public SupplementalsListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IndividualSupplementalHollow instantiateElement(int ordinal) {
        return (IndividualSupplementalHollow) api().getIndividualSupplementalHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SupplementalsListTypeAPI typeApi() {
        return (SupplementalsListTypeAPI) delegate.getTypeAPI();
    }

}