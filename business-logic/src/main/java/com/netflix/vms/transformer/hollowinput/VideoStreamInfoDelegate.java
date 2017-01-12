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

    public long getVmafScore(int ordinal);

    public Long getVmafScoreBoxed(int ordinal);

    public long getScaledPsnrTimesHundred(int ordinal);

    public Long getScaledPsnrTimesHundredBoxed(int ordinal);

    public float getFps(int ordinal);

    public Float getFpsBoxed(int ordinal);

    public VideoStreamInfoTypeAPI getTypeAPI();

}