package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoStreamInfoHollow extends HollowObject {

    public VideoStreamInfoHollow(VideoStreamInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getVideoBitrateKBPS() {
        return delegate().getVideoBitrateKBPS(ordinal);
    }

    public Integer _getVideoBitrateKBPSBoxed() {
        return delegate().getVideoBitrateKBPSBoxed(ordinal);
    }

    public long _getDashHeaderSize() {
        return delegate().getDashHeaderSize(ordinal);
    }

    public Long _getDashHeaderSizeBoxed() {
        return delegate().getDashHeaderSizeBoxed(ordinal);
    }

    public long _getDashMediaStartByteOffset() {
        return delegate().getDashMediaStartByteOffset(ordinal);
    }

    public Long _getDashMediaStartByteOffsetBoxed() {
        return delegate().getDashMediaStartByteOffsetBoxed(ordinal);
    }

    public long _getVmafScore() {
        return delegate().getVmafScore(ordinal);
    }

    public Long _getVmafScoreBoxed() {
        return delegate().getVmafScoreBoxed(ordinal);
    }

    public long _getScaledPsnrTimesHundred() {
        return delegate().getScaledPsnrTimesHundred(ordinal);
    }

    public Long _getScaledPsnrTimesHundredBoxed() {
        return delegate().getScaledPsnrTimesHundredBoxed(ordinal);
    }

    public float _getFps() {
        return delegate().getFps(ordinal);
    }

    public Float _getFpsBoxed() {
        return delegate().getFpsBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoStreamInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoStreamInfoDelegate delegate() {
        return (VideoStreamInfoDelegate)delegate;
    }

}