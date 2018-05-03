package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class VideoTypeDescriptorHollow extends HollowObject {

    public VideoTypeDescriptorHollow(VideoTypeDescriptorDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCopyright() {
        int refOrdinal = delegate().getCopyrightOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getTierType() {
        int refOrdinal = delegate().getTierTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getOriginal() {
        return delegate().getOriginal(ordinal);
    }

    public Boolean _getOriginalBoxed() {
        return delegate().getOriginalBoxed(ordinal);
    }

    public VideoTypeMediaListHollow _getMedia() {
        int refOrdinal = delegate().getMediaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeMediaListHollow(refOrdinal);
    }

    public boolean _getExtended() {
        return delegate().getExtended(ordinal);
    }

    public Boolean _getExtendedBoxed() {
        return delegate().getExtendedBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeDescriptorTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDescriptorDelegate delegate() {
        return (VideoTypeDescriptorDelegate)delegate;
    }

}