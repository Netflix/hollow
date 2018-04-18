package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseArtworkHollow extends HollowObject {

    public RolloutPhaseArtworkHollow(RolloutPhaseArtworkDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhaseArtworkSourceFileIdListHollow _getSourceFileIds() {
        int refOrdinal = delegate().getSourceFileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseArtworkSourceFileIdListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseArtworkDelegate delegate() {
        return (RolloutPhaseArtworkDelegate)delegate;
    }

}