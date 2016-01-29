package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow extends HollowList<RolloutPhasesElementsArtwork_newSourceFileIdsHollow> {

    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhasesElementsArtwork_newSourceFileIdsHollow instantiateElement(int ordinal) {
        return (RolloutPhasesElementsArtwork_newSourceFileIdsHollow) api().getRolloutPhasesElementsArtwork_newSourceFileIdsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI typeApi() {
        return (RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI) delegate.getTypeAPI();
    }

}