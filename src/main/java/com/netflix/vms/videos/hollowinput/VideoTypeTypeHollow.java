package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoTypeTypeHollow extends HollowObject {

    public VideoTypeTypeHollow(VideoTypeTypeDelegate delegate, int ordinal) {
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

    public VideoTypeTypeArrayOfMediaHollow _getMedia() {
        int refOrdinal = delegate().getMediaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoTypeTypeArrayOfMediaHollow(refOrdinal);
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

    public VideoTypeTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoTypeTypeDelegate delegate() {
        return (VideoTypeTypeDelegate)delegate;
    }

}