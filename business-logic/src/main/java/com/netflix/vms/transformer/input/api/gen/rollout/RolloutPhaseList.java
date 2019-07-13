package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseList extends HollowList<RolloutPhase> {

    public RolloutPhaseList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RolloutPhase instantiateElement(int ordinal) {
        return (RolloutPhase) api().getRolloutPhase(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseListTypeAPI typeApi() {
        return (RolloutPhaseListTypeAPI) delegate.getTypeAPI();
    }

}