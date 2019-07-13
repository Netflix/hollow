package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhase extends HollowObject {

    public RolloutPhase(RolloutPhaseDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getSeasonMovieId() {
        return delegate().getSeasonMovieId(ordinal);
    }

    public Long getSeasonMovieIdBoxed() {
        return delegate().getSeasonMovieIdBoxed(ordinal);
    }

    public RolloutPhaseElements getElements() {
        int refOrdinal = delegate().getElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseElements(refOrdinal);
    }

    public String getName() {
        return delegate().getName(ordinal);
    }

    public boolean isNameEqual(String testValue) {
        return delegate().isNameEqual(ordinal, testValue);
    }

    public HString getNameHollowReference() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getShowCoreMetadata() {
        return delegate().getShowCoreMetadata(ordinal);
    }

    public Boolean getShowCoreMetadataBoxed() {
        return delegate().getShowCoreMetadataBoxed(ordinal);
    }

    public RolloutPhaseWindowMap getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhaseWindowMap(refOrdinal);
    }

    public String getPhaseType() {
        return delegate().getPhaseType(ordinal);
    }

    public boolean isPhaseTypeEqual(String testValue) {
        return delegate().isPhaseTypeEqual(ordinal, testValue);
    }

    public HString getPhaseTypeHollowReference() {
        int refOrdinal = delegate().getPhaseTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseDelegate delegate() {
        return (RolloutPhaseDelegate)delegate;
    }

}