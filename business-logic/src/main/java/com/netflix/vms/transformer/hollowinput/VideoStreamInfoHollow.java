package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
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

    public int _getVideoPeakBitrateKBPS() {
        return delegate().getVideoPeakBitrateKBPS(ordinal);
    }

    public Integer _getVideoPeakBitrateKBPSBoxed() {
        return delegate().getVideoPeakBitrateKBPSBoxed(ordinal);
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

    public DashStreamHeaderDataHollow _getDashStreamHeaderData() {
        int refOrdinal = delegate().getDashStreamHeaderDataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDashStreamHeaderDataHollow(refOrdinal);
    }

    public long _getVmafScore() {
        return delegate().getVmafScore(ordinal);
    }

    public Long _getVmafScoreBoxed() {
        return delegate().getVmafScoreBoxed(ordinal);
    }

    public int _getVmafAlgoVersionExp() {
        return delegate().getVmafAlgoVersionExp(ordinal);
    }

    public Integer _getVmafAlgoVersionExpBoxed() {
        return delegate().getVmafAlgoVersionExpBoxed(ordinal);
    }

    public int _getVmafAlgoVersionLts() {
        return delegate().getVmafAlgoVersionLts(ordinal);
    }

    public Integer _getVmafAlgoVersionLtsBoxed() {
        return delegate().getVmafAlgoVersionLtsBoxed(ordinal);
    }

    public int _getVmafScoreExp() {
        return delegate().getVmafScoreExp(ordinal);
    }

    public Integer _getVmafScoreExpBoxed() {
        return delegate().getVmafScoreExpBoxed(ordinal);
    }

    public int _getVmafScoreLts() {
        return delegate().getVmafScoreLts(ordinal);
    }

    public Integer _getVmafScoreLtsBoxed() {
        return delegate().getVmafScoreLtsBoxed(ordinal);
    }

    public int _getVmafplusScoreExp() {
        return delegate().getVmafplusScoreExp(ordinal);
    }

    public Integer _getVmafplusScoreExpBoxed() {
        return delegate().getVmafplusScoreExpBoxed(ordinal);
    }

    public int _getVmafplusScoreLts() {
        return delegate().getVmafplusScoreLts(ordinal);
    }

    public Integer _getVmafplusScoreLtsBoxed() {
        return delegate().getVmafplusScoreLtsBoxed(ordinal);
    }

    public int _getVmafplusPhoneScoreExp() {
        return delegate().getVmafplusPhoneScoreExp(ordinal);
    }

    public Integer _getVmafplusPhoneScoreExpBoxed() {
        return delegate().getVmafplusPhoneScoreExpBoxed(ordinal);
    }

    public int _getVmafplusPhoneScoreLts() {
        return delegate().getVmafplusPhoneScoreLts(ordinal);
    }

    public Integer _getVmafplusPhoneScoreLtsBoxed() {
        return delegate().getVmafplusPhoneScoreLtsBoxed(ordinal);
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

    public VideoStreamCropParamsHollow _getCropParams() {
        int refOrdinal = delegate().getCropParamsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoStreamCropParamsHollow(refOrdinal);
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