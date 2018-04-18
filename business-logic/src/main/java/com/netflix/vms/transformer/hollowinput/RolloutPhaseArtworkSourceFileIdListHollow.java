package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdListHollow extends HollowList<RolloutPhaseArtworkSourceFileIdHollow> {

    public RolloutPhaseArtworkSourceFileIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RolloutPhaseArtworkSourceFileIdHollow instantiateElement(int ordinal) {
        return (RolloutPhaseArtworkSourceFileIdHollow) api().getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdListTypeAPI typeApi() {
        return (RolloutPhaseArtworkSourceFileIdListTypeAPI) delegate.getTypeAPI();
    }

}