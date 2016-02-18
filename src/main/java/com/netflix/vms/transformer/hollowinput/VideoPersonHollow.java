package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonHollow extends HollowObject {

    public VideoPersonHollow(VideoPersonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public VideoPersonCastListHollow _getCast() {
        int refOrdinal = delegate().getCastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoPersonCastListHollow(refOrdinal);
    }

    public VideoPersonAliasListHollow _getAlias() {
        int refOrdinal = delegate().getAliasOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoPersonAliasListHollow(refOrdinal);
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