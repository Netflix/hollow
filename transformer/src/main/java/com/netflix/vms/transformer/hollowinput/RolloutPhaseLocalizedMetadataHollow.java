package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseLocalizedMetadataHollow extends HollowObject {

    public RolloutPhaseLocalizedMetadataHollow(RolloutPhaseLocalizedMetadataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getSUPPLEMENTAL_MESSAGE() {
        int refOrdinal = delegate().getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getTAGLINE() {
        int refOrdinal = delegate().getTAGLINEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseLocalizedMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseLocalizedMetadataDelegate delegate() {
        return (RolloutPhaseLocalizedMetadataDelegate)delegate;
    }

}