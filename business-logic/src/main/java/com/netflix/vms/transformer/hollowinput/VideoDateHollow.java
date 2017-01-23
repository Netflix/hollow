package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoDateHollow extends HollowObject {

    public VideoDateHollow(VideoDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoDateWindowListHollow _getWindow() {
        int refOrdinal = delegate().getWindowOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoDateWindowListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDateDelegate delegate() {
        return (VideoDateDelegate)delegate;
    }

}