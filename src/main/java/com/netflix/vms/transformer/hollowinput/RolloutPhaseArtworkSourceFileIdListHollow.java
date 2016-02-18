package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseArtworkSourceFileIdListHollow extends HollowList<RolloutPhaseArtworkSourceFileIdHollow> {

    public RolloutPhaseArtworkSourceFileIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseArtworkSourceFileIdHollow instantiateElement(int ordinal) {
        return (RolloutPhaseArtworkSourceFileIdHollow) api().getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdListTypeAPI typeApi() {
        return (RolloutPhaseArtworkSourceFileIdListTypeAPI) delegate.getTypeAPI();
    }

}