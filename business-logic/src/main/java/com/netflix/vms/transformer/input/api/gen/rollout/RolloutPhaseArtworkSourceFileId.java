package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileId extends HollowObject {

    public RolloutPhaseArtworkSourceFileId(RolloutPhaseArtworkSourceFileIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public HString getValueHollowReference() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseArtworkSourceFileIdDelegate delegate() {
        return (RolloutPhaseArtworkSourceFileIdDelegate)delegate;
    }

}