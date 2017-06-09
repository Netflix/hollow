package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoStreamInfoDelegate extends HollowObjectDelegate {

    public int getVideoBitrateKBPS(int ordinal);

    public Integer getVideoBitrateKBPSBoxed(int ordinal);

    public int getVideoPeakBitrateKBPS(int ordinal);

    public Integer getVideoPeakBitrateKBPSBoxed(int ordinal);

    public long getDashHeaderSize(int ordinal);

    public Long getDashHeaderSizeBoxed(int ordinal);

    public long getDashMediaStartByteOffset(int ordinal);

    public Long getDashMediaStartByteOffsetBoxed(int ordinal);

    public int getDashStreamHeaderDataOrdinal(int ordinal);

    public long getVmafScore(int ordinal);

    public Long getVmafScoreBoxed(int ordinal);

    public int getVmafAlgoVersionExp(int ordinal);

    public Integer getVmafAlgoVersionExpBoxed(int ordinal);

    public int getVmafAlgoVersionLts(int ordinal);

    public Integer getVmafAlgoVersionLtsBoxed(int ordinal);

    public int getVmafScoreExp(int ordinal);

    public Integer getVmafScoreExpBoxed(int ordinal);

    public int getVmafScoreLts(int ordinal);

    public Integer getVmafScoreLtsBoxed(int ordinal);

    public int getVmafplusScoreExp(int ordinal);

    public Integer getVmafplusScoreExpBoxed(int ordinal);

    public int getVmafplusScoreLts(int ordinal);

    public Integer getVmafplusScoreLtsBoxed(int ordinal);

    public int getVmafplusPhoneScoreExp(int ordinal);

    public Integer getVmafplusPhoneScoreExpBoxed(int ordinal);

    public int getVmafplusPhoneScoreLts(int ordinal);

    public Integer getVmafplusPhoneScoreLtsBoxed(int ordinal);

    public long getScaledPsnrTimesHundred(int ordinal);

    public Long getScaledPsnrTimesHundredBoxed(int ordinal);

    public float getFps(int ordinal);

    public Float getFpsBoxed(int ordinal);

    public int getCropParamsOrdinal(int ordinal);

    public VideoStreamInfoTypeAPI getTypeAPI();

}