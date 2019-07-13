package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdList extends HollowList<RolloutPhaseArtworkSourceFileId> {

    public RolloutPhaseArtworkSourceFileIdList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RolloutPhaseArtworkSourceFileId instantiateElement(int ordinal) {
        return (RolloutPhaseArtworkSourceFileId) api().getRolloutPhaseArtworkSourceFileId(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdListTypeAPI typeApi() {
        return (RolloutPhaseArtworkSourceFileIdListTypeAPI) delegate.getTypeAPI();
    }

}