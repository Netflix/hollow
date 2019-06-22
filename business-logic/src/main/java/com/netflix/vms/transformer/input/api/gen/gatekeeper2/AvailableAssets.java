package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class AvailableAssets extends HollowObject {

    public AvailableAssets(AvailableAssetsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public SetOfString getAvailableSubs() {
        int refOrdinal = delegate().getAvailableSubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfString getAvailableDubs() {
        int refOrdinal = delegate().getAvailableDubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfString getBlockedSubs() {
        int refOrdinal = delegate().getBlockedSubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfString getBlockedDubs() {
        int refOrdinal = delegate().getBlockedDubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfString getMissingSubs() {
        int refOrdinal = delegate().getMissingSubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfString getMissingDubs() {
        int refOrdinal = delegate().getMissingDubsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public AvailableAssetsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AvailableAssetsDelegate delegate() {
        return (AvailableAssetsDelegate)delegate;
    }

}