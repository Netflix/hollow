package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonHollow extends HollowObject {

    public VideoPersonHollow(VideoPersonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoPersonArrayOfCastHollow _getCast() {
        int refOrdinal = delegate().getCastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoPersonArrayOfCastHollow(refOrdinal);
    }

    public VideoPersonArrayOfAliasHollow _getAlias() {
        int refOrdinal = delegate().getAliasOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoPersonArrayOfAliasHollow(refOrdinal);
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

    public VideoPersonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoPersonDelegate delegate() {
        return (VideoPersonDelegate)delegate;
    }

}