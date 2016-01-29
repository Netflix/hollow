package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsLocalized_metadataHollow extends HollowObject {

    public RolloutPhasesElementsLocalized_metadataHollow(RolloutPhasesElementsLocalized_metadataDelegate delegate, int ordinal) {
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

    public RolloutPhasesElementsLocalized_metadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsLocalized_metadataDelegate delegate() {
        return (RolloutPhasesElementsLocalized_metadataDelegate)delegate;
    }

}