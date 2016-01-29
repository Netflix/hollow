package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsArrayOfTrailersHollow extends HollowList<RolloutPhasesElementsTrailersHollow> {

    public RolloutPhasesElementsArrayOfTrailersHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesElementsTrailersHollow instantiateElement(int ordinal) {
        return (RolloutPhasesElementsTrailersHollow) api().getRolloutPhasesElementsTrailersHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArrayOfTrailersTypeAPI typeApi() {
        return (RolloutPhasesElementsArrayOfTrailersTypeAPI) delegate.getTypeAPI();
    }

}