package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhaseCharacterListHollow extends HollowList<RolloutPhaseCharacterHollow> {

    public RolloutPhaseCharacterListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseCharacterHollow instantiateElement(int ordinal) {
        return (RolloutPhaseCharacterHollow) api().getRolloutPhaseCharacterHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseCharacterListTypeAPI typeApi() {
        return (RolloutPhaseCharacterListTypeAPI) delegate.getTypeAPI();
    }

}