package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseCastListHollow extends HollowList<RolloutPhaseCastHollow> {

    public RolloutPhaseCastListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseCastHollow instantiateElement(int ordinal) {
        return (RolloutPhaseCastHollow) api().getRolloutPhaseCastHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseCastListTypeAPI typeApi() {
        return (RolloutPhaseCastListTypeAPI) delegate.getTypeAPI();
    }

}