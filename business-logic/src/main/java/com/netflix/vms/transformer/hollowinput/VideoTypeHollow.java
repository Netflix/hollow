package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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

    public VideoTypeDescriptorSetHollow _getCountryInfos() {
        int refOrdinal = delegate().getCountryInfosOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeDescriptorSetHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDelegate delegate() {
        return (VideoTypeDelegate)delegate;
    }

}