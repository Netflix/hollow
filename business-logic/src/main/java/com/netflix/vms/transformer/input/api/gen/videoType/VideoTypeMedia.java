package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoTypeMedia extends HollowObject {

    public VideoTypeMedia(VideoTypeMediaDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public HString getValueHollowReference() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public VideoTypeAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeMediaTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeMediaDelegate delegate() {
        return (VideoTypeMediaDelegate)delegate;
    }

}