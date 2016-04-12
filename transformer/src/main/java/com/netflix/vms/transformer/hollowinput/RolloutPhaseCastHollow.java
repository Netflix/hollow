package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseCastHollow extends HollowObject {

    public RolloutPhaseCastHollow(RolloutPhaseCastDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseCastTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseCastDelegate delegate() {
        return (RolloutPhaseCastDelegate)delegate;
    }

}