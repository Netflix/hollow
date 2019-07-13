package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoGeneralTitleType extends HollowObject {

    public VideoGeneralTitleType(VideoGeneralTitleTypeDelegate delegate, int ordinal) {
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

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTitleTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralTitleTypeDelegate delegate() {
        return (VideoGeneralTitleTypeDelegate)delegate;
    }

}