package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsHollow extends HollowObject {

    public AwardsHollow(AwardsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAwardId() {
        return delegate().getAwardId(ordinal);
    }

    public Long _getAwardIdBoxed() {
        return delegate().getAwardIdBoxed(ordinal);
    }

    public AwardsDescriptionHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsDescriptionHollow(refOrdinal);
    }

    public AwardsAlternateNameHollow _getAlternateName() {
        int refOrdinal = delegate().getAlternateNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsAlternateNameHollow(refOrdinal);
    }

    public AwardsAwardNameHollow _getAwardName() {
        int refOrdinal = delegate().getAwardNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsAwardNameHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsDelegate delegate() {
        return (AwardsDelegate)delegate;
    }

}