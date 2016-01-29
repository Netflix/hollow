package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsArtworkHollow extends HollowObject {

    public RolloutPhasesElementsArtworkHollow(RolloutPhasesElementsArtworkDelegate delegate, int ordinal) {
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

    public RolloutPhasesElementsArtworkTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsArtworkDelegate delegate() {
        return (RolloutPhasesElementsArtworkDelegate)delegate;
    }

}