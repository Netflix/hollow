package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseListHollow extends HollowList<RolloutPhaseHollow> {

    public RolloutPhaseListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RolloutPhaseHollow instantiateElement(int ordinal) {
        return (RolloutPhaseHollow) api().getRolloutPhaseHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseListTypeAPI typeApi() {
        return (RolloutPhaseListTypeAPI) delegate.getTypeAPI();
    }

}