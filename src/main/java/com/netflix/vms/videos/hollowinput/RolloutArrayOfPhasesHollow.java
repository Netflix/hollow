package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutArrayOfPhasesHollow extends HollowList<RolloutPhasesHollow> {

    public RolloutArrayOfPhasesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesHollow instantiateElement(int ordinal) {
        return (RolloutPhasesHollow) api().getRolloutPhasesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutArrayOfPhasesTypeAPI typeApi() {
        return (RolloutArrayOfPhasesTypeAPI) delegate.getTypeAPI();
    }

}