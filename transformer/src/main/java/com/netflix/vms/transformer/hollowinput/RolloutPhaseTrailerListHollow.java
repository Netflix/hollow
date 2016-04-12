package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseTrailerListHollow extends HollowList<RolloutPhaseTrailerHollow> {

    public RolloutPhaseTrailerListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseTrailerHollow instantiateElement(int ordinal) {
        return (RolloutPhaseTrailerHollow) api().getRolloutPhaseTrailerHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseTrailerListTypeAPI typeApi() {
        return (RolloutPhaseTrailerListTypeAPI) delegate.getTypeAPI();
    }

}