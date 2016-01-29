package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsArrayOfCharactersHollow extends HollowList<RolloutPhasesElementsCharactersHollow> {

    public RolloutPhasesElementsArrayOfCharactersHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesElementsCharactersHollow instantiateElement(int ordinal) {
        return (RolloutPhasesElementsCharactersHollow) api().getRolloutPhasesElementsCharactersHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArrayOfCharactersTypeAPI typeApi() {
        return (RolloutPhasesElementsArrayOfCharactersTypeAPI) delegate.getTypeAPI();
    }

}