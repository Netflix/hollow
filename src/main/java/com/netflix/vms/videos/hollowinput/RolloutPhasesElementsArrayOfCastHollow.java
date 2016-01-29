package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsArrayOfCastHollow extends HollowList<RolloutPhasesElementsCastHollow> {

    public RolloutPhasesElementsArrayOfCastHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesElementsCastHollow instantiateElement(int ordinal) {
        return (RolloutPhasesElementsCastHollow) api().getRolloutPhasesElementsCastHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArrayOfCastTypeAPI typeApi() {
        return (RolloutPhasesElementsArrayOfCastTypeAPI) delegate.getTypeAPI();
    }

}