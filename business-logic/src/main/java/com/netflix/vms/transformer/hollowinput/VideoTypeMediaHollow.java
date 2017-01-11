package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoTypeMediaHollow extends HollowObject {

    public VideoTypeMediaHollow(VideoTypeMediaDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeMediaTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeMediaDelegate delegate() {
        return (VideoTypeMediaDelegate)delegate;
    }

}