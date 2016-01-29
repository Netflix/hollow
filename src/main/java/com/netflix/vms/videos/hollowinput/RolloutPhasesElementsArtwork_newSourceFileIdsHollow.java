package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsArtwork_newSourceFileIdsHollow extends HollowObject {

    public RolloutPhasesElementsArtwork_newSourceFileIdsHollow(RolloutPhasesElementsArtwork_newSourceFileIdsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsArtwork_newSourceFileIdsDelegate delegate() {
        return (RolloutPhasesElementsArtwork_newSourceFileIdsDelegate)delegate;
    }

}