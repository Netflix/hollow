package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonAliasHollow extends HollowObject {

    public VideoPersonAliasHollow(VideoPersonAliasDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAliasId() {
        return delegate().getAliasId(ordinal);
    }

    public Long _getAliasIdBoxed() {
        return delegate().getAliasIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoPersonAliasTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoPersonAliasDelegate delegate() {
        return (VideoPersonAliasDelegate)delegate;
    }

}