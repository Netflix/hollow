package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoTypeDescriptor extends HollowObject {

    public VideoTypeDescriptor(VideoTypeDescriptorDelegate delegate, int ordinal) {
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

    public String getCopyright() {
        return delegate().getCopyright(ordinal);
    }

    public boolean isCopyrightEqual(String testValue) {
        return delegate().isCopyrightEqual(ordinal, testValue);
    }

    public HString getCopyrightHollowReference() {
        int refOrdinal = delegate().getCopyrightOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getTierType() {
        return delegate().getTierType(ordinal);
    }

    public boolean isTierTypeEqual(String testValue) {
        return delegate().isTierTypeEqual(ordinal, testValue);
    }

    public HString getTierTypeHollowReference() {
        int refOrdinal = delegate().getTierTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getOriginal() {
        return delegate().getOriginal(ordinal);
    }

    public Boolean getOriginalBoxed() {
        return delegate().getOriginalBoxed(ordinal);
    }

    public VideoTypeMediaList getMedia() {
        int refOrdinal = delegate().getMediaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeMediaList(refOrdinal);
    }

    public boolean getExtended() {
        return delegate().getExtended(ordinal);
    }

    public Boolean getExtendedBoxed() {
        return delegate().getExtendedBoxed(ordinal);
    }

    public VideoTypeAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeDescriptorTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDescriptorDelegate delegate() {
        return (VideoTypeDescriptorDelegate)delegate;
    }

}