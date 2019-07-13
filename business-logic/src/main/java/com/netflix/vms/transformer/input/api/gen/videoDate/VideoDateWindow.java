package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoDateWindow extends HollowObject {

    public VideoDateWindow(VideoDateWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getCountryCode() {
        return delegate().getCountryCode(ordinal);
    }

    public boolean isCountryCodeEqual(String testValue) {
        return delegate().isCountryCodeEqual(ordinal, testValue);
    }

    public HString getCountryCodeHollowReference() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public ListOfReleaseDates getReleaseDates() {
        int refOrdinal = delegate().getReleaseDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfReleaseDates(refOrdinal);
    }

    public VideoDateAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoDateWindowDelegate delegate() {
        return (VideoDateWindowDelegate)delegate;
    }

}