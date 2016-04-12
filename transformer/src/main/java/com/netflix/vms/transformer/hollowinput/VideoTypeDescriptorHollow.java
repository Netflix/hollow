package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeDescriptorHollow extends HollowObject {

    public VideoTypeDescriptorHollow(VideoTypeDescriptorDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getIsOriginal() {
        return delegate().getIsOriginal(ordinal);
    }

    public Boolean _getIsOriginalBoxed() {
        return delegate().getIsOriginalBoxed(ordinal);
    }

    public long _getShowMemberTypeId() {
        return delegate().getShowMemberTypeId(ordinal);
    }

    public Long _getShowMemberTypeIdBoxed() {
        return delegate().getShowMemberTypeIdBoxed(ordinal);
    }

    public StringHollow _getCopyright() {
        int refOrdinal = delegate().getCopyrightOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getIsContentApproved() {
        return delegate().getIsContentApproved(ordinal);
    }

    public Boolean _getIsContentApprovedBoxed() {
        return delegate().getIsContentApprovedBoxed(ordinal);
    }

    public VideoTypeMediaListHollow _getMedia() {
        int refOrdinal = delegate().getMediaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeMediaListHollow(refOrdinal);
    }

    public boolean _getIsCanon() {
        return delegate().getIsCanon(ordinal);
    }

    public Boolean _getIsCanonBoxed() {
        return delegate().getIsCanonBoxed(ordinal);
    }

    public boolean _getIsExtended() {
        return delegate().getIsExtended(ordinal);
    }

    public Boolean _getIsExtendedBoxed() {
        return delegate().getIsExtendedBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeDescriptorTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeDescriptorDelegate delegate() {
        return (VideoTypeDescriptorDelegate)delegate;
    }

}