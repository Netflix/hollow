package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoPersonCastHollow extends HollowObject {

    public VideoPersonCastHollow(VideoPersonCastDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public long _getRoleTypeId() {
        return delegate().getRoleTypeId(ordinal);
    }

    public Long _getRoleTypeIdBoxed() {
        return delegate().getRoleTypeIdBoxed(ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public StringHollow _getRoleName() {
        int refOrdinal = delegate().getRoleNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoPersonCastTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoPersonCastDelegate delegate() {
        return (VideoPersonCastDelegate)delegate;
    }

}