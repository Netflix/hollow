package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoAwardHollow extends HollowObject {

    public VideoAwardHollow(VideoAwardDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoAwardListHollow _getAward() {
        int refOrdinal = delegate().getAwardOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoAwardListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoAwardDelegate delegate() {
        return (VideoAwardDelegate)delegate;
    }

}