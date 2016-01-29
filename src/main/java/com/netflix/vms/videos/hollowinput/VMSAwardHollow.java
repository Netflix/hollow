package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VMSAwardHollow extends HollowObject {

    public VMSAwardHollow(VMSAwardDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAwardId() {
        return delegate().getAwardId(ordinal);
    }

    public Long _getAwardIdBoxed() {
        return delegate().getAwardIdBoxed(ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getFestivalId() {
        return delegate().getFestivalId(ordinal);
    }

    public Long _getFestivalIdBoxed() {
        return delegate().getFestivalIdBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getIsMovieAward() {
        return delegate().getIsMovieAward(ordinal);
    }

    public Boolean _getIsMovieAwardBoxed() {
        return delegate().getIsMovieAwardBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VMSAwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VMSAwardDelegate delegate() {
        return (VMSAwardDelegate)delegate;
    }

}