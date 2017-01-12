package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
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

    public StringHollow _getMERCH_OVERRIDE_MESSAGE() {
        int refOrdinal = delegate().getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getPOSTPLAY_OVERRIDE_MESSAGE() {
        int refOrdinal = delegate().getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getODP_OVERRIDE_MESSAGE() {
        int refOrdinal = delegate().getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseLocalizedMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseLocalizedMetadataDelegate delegate() {
        return (RolloutPhaseLocalizedMetadataDelegate)delegate;
    }

}