package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseListHollow extends HollowList<RolloutPhaseHollow> {

    public RolloutPhaseListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseHollow instantiateElement(int ordinal) {
        return (RolloutPhaseHollow) api().getRolloutPhaseHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseListTypeAPI typeApi() {
        return (RolloutPhaseListTypeAPI) delegate.getTypeAPI();
    }

}