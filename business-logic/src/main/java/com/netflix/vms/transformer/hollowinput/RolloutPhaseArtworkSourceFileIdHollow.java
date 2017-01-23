package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdHollow extends HollowObject {

    public RolloutPhaseArtworkSourceFileIdHollow(RolloutPhaseArtworkSourceFileIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseArtworkSourceFileIdDelegate delegate() {
        return (RolloutPhaseArtworkSourceFileIdDelegate)delegate;
    }

}