package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsArtwork_newHollow extends HollowObject {

    public RolloutPhasesElementsArtwork_newHollow(RolloutPhasesElementsArtwork_newDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow _getSourceFileIds() {
        int refOrdinal = delegate().getSourceFileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArtwork_newTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsArtwork_newDelegate delegate() {
        return (RolloutPhasesElementsArtwork_newDelegate)delegate;
    }

}