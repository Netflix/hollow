package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoAwardAwardHollow extends HollowObject {

    public VideoAwardAwardHollow(VideoAwardAwardDelegate delegate, int ordinal) {
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

    public boolean _getWinner() {
        return delegate().getWinner(ordinal);
    }

    public Boolean _getWinnerBoxed() {
        return delegate().getWinnerBoxed(ordinal);
    }

    public long _getYear() {
        return delegate().getYear(ordinal);
    }

    public Long _getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public long _getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long _getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardAwardTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoAwardAwardDelegate delegate() {
        return (VideoAwardAwardDelegate)delegate;
    }

}