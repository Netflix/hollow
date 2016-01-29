package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetSetsChildrenHollow extends HollowObject {

    public VideoDisplaySetSetsChildrenHollow(VideoDisplaySetSetsChildrenDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public VideoDisplaySetSetsChildrenArrayOfChildrenHollow _getChildren() {
        int refOrdinal = delegate().getChildrenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoDisplaySetSetsChildrenArrayOfChildrenHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDisplaySetSetsChildrenTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDisplaySetSetsChildrenDelegate delegate() {
        return (VideoDisplaySetSetsChildrenDelegate)delegate;
    }

}