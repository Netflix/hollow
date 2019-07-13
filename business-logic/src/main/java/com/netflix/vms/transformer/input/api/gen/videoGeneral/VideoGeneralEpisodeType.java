package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoGeneralEpisodeType extends HollowObject {

    public VideoGeneralEpisodeType(VideoGeneralEpisodeTypeDelegate delegate, int ordinal) {
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

    public String getCountry() {
        return delegate().getCountry(ordinal);
    }

    public boolean isCountryEqual(String testValue) {
        return delegate().isCountryEqual(ordinal, testValue);
    }

    public HString getCountryHollowReference() {
        int refOrdinal = delegate().getCountryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralEpisodeTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralEpisodeTypeDelegate delegate() {
        return (VideoGeneralEpisodeTypeDelegate)delegate;
    }

}