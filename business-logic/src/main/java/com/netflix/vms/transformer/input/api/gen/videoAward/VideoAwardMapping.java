package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoAwardMapping extends HollowObject {

    public VideoAwardMapping(VideoAwardMappingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getAwardId() {
        return delegate().getAwardId(ordinal);
    }

    public Long getAwardIdBoxed() {
        return delegate().getAwardIdBoxed(ordinal);
    }

    public long getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public long getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public boolean getWinner() {
        return delegate().getWinner(ordinal);
    }

    public Boolean getWinnerBoxed() {
        return delegate().getWinnerBoxed(ordinal);
    }

    public long getYear() {
        return delegate().getYear(ordinal);
    }

    public Long getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public VideoAwardAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardMappingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoAwardMappingDelegate delegate() {
        return (VideoAwardMappingDelegate)delegate;
    }

}