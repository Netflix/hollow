package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoAwardMappingHollow extends HollowObject {

    public VideoAwardMappingHollow(VideoAwardMappingDelegate delegate, int ordinal) {
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

    public boolean _getWinner() {
        return delegate().getWinner(ordinal);
    }

    public Boolean _getWinnerBoxed() {
        return delegate().getWinnerBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardMappingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoAwardMappingDelegate delegate() {
        return (VideoAwardMappingDelegate)delegate;
    }

}