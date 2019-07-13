package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseArtwork extends HollowObject {

    public RolloutPhaseArtwork(RolloutPhaseArtworkDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RolloutPhaseArtworkSourceFileIdList getSourceFileIds() {
        int refOrdinal = delegate().getSourceFileIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseArtworkSourceFileIdList(refOrdinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseArtworkDelegate delegate() {
        return (RolloutPhaseArtworkDelegate)delegate;
    }

}