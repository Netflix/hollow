package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeHollow extends HollowObject {

    public VideoTypeHollow(VideoTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoTypeArrayOfTypeHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeArrayOfTypeHollow(refOrdinal);
    }

    public boolean _getIsTV() {
        return delegate().getIsTV(ordinal);
    }

    public Boolean _getIsTVBoxed() {
        return delegate().getIsTVBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDelegate delegate() {
        return (VideoTypeDelegate)delegate;
    }

}