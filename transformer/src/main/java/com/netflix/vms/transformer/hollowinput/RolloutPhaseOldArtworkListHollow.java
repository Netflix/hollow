package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseOldArtworkListHollow extends HollowList<RolloutPhaseImageIdHollow> {

    public RolloutPhaseOldArtworkListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseImageIdHollow instantiateElement(int ordinal) {
        return (RolloutPhaseImageIdHollow) api().getRolloutPhaseImageIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseOldArtworkListTypeAPI typeApi() {
        return (RolloutPhaseOldArtworkListTypeAPI) delegate.getTypeAPI();
    }

}