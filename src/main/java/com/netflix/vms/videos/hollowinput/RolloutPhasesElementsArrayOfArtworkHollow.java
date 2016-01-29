package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsArrayOfArtworkHollow extends HollowList<RolloutPhasesElementsArtworkHollow> {

    public RolloutPhasesElementsArrayOfArtworkHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesElementsArtworkHollow instantiateElement(int ordinal) {
        return (RolloutPhasesElementsArtworkHollow) api().getRolloutPhasesElementsArtworkHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArrayOfArtworkTypeAPI typeApi() {
        return (RolloutPhasesElementsArrayOfArtworkTypeAPI) delegate.getTypeAPI();
    }

}