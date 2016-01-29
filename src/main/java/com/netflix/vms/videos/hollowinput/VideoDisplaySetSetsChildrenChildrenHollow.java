package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetSetsChildrenChildrenHollow extends HollowObject {

    public VideoDisplaySetSetsChildrenChildrenHollow(VideoDisplaySetSetsChildrenChildrenDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getParentSequenceNumber() {
        return delegate().getParentSequenceNumber(ordinal);
    }

    public Long _getParentSequenceNumberBoxed() {
        return delegate().getParentSequenceNumberBoxed(ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getAltId() {
        return delegate().getAltId(ordinal);
    }

    public Long _getAltIdBoxed() {
        return delegate().getAltIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDisplaySetSetsChildrenChildrenTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDisplaySetSetsChildrenChildrenDelegate delegate() {
        return (VideoDisplaySetSetsChildrenChildrenDelegate)delegate;
    }

}