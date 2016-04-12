package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseNewArtworkHollow extends HollowObject {

    public RolloutPhaseNewArtworkHollow(RolloutPhaseNewArtworkDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhaseArtworkSourceFileIdListHollow _getSourceFileIds() {
        int refOrdinal = delegate().getSourceFileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseArtworkSourceFileIdListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseNewArtworkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseNewArtworkDelegate delegate() {
        return (RolloutPhaseNewArtworkDelegate)delegate;
    }

}