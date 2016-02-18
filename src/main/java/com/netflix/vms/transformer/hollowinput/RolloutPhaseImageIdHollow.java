package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseImageIdHollow extends HollowObject {

    public RolloutPhaseImageIdHollow(RolloutPhaseImageIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getImageId() {
        return delegate().getImageId(ordinal);
    }

    public Long _getImageIdBoxed() {
        return delegate().getImageIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseImageIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseImageIdDelegate delegate() {
        return (RolloutPhaseImageIdDelegate)delegate;
    }

}