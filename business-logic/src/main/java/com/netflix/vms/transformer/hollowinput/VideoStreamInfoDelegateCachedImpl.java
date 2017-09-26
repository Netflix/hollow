package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoStreamInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoStreamInfoDelegate {

    private final Integer videoBitrateKBPS;
    private final Integer videoPeakBitrateKBPS;
    private final Long dashHeaderSize;
    private final Long dashMediaStartByteOffset;
    private final int dashStreamHeaderDataOrdinal;
    private final Long vmafScore;
    private final Integer vmafAlgoVersionExp;
    private final Integer vmafAlgoVersionLts;
    private final Integer vmafScoreExp;
    private final Integer vmafScoreLts;
    private final Integer vmafplusScoreExp;
    private final Integer vmafplusScoreLts;
    private final Integer vmafplusPhoneScoreExp;
    private final Integer vmafplusPhoneScoreLts;
    private final Long scaledPsnrTimesHundred;
    private final Float fps;
    private final int cropParamsOrdinal;
    private VideoStreamInfoTypeAPI typeAPI;

    public VideoStreamInfoDelegateCachedImpl(VideoStreamInfoTypeAPI typeAPI, int ordinal) {
        this.videoBitrateKBPS = typeAPI.getVideoBitrateKBPSBoxed(ordinal);
        this.videoPeakBitrateKBPS = typeAPI.getVideoPeakBitrateKBPSBoxed(ordinal);
        this.dashHeaderSize = typeAPI.getDashHeaderSizeBoxed(ordinal);
        this.dashMediaStartByteOffset = typeAPI.getDashMediaStartByteOffsetBoxed(ordinal);
        this.dashStreamHeaderDataOrdinal = typeAPI.getDashStreamHeaderDataOrdinal(ordinal);
        this.vmafScore = typeAPI.getVmafScoreBoxed(ordinal);
        this.vmafAlgoVersionExp = typeAPI.getVmafAlgoVersionExpBoxed(ordinal);
        this.vmafAlgoVersionLts = typeAPI.getVmafAlgoVersionLtsBoxed(ordinal);
        this.vmafScoreExp = typeAPI.getVmafScoreExpBoxed(ordinal);
        this.vmafScoreLts = typeAPI.getVmafScoreLtsBoxed(ordinal);
        this.vmafplusScoreExp = typeAPI.getVmafplusScoreExpBoxed(ordinal);
        this.vmafplusScoreLts = typeAPI.getVmafplusScoreLtsBoxed(ordinal);
        this.vmafplusPhoneScoreExp = typeAPI.getVmafplusPhoneScoreExpBoxed(ordinal);
        this.vmafplusPhoneScoreLts = typeAPI.getVmafplusPhoneScoreLtsBoxed(ordinal);
        this.scaledPsnrTimesHundred = typeAPI.getScaledPsnrTimesHundredBoxed(ordinal);
        this.fps = typeAPI.getFpsBoxed(ordinal);
        this.cropParamsOrdinal = typeAPI.getCropParamsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getVideoBitrateKBPS(int ordinal) {
        if(videoBitrateKBPS == null)
            return Integer.MIN_VALUE;
        return videoBitrateKBPS.intValue();
    }

    public Integer getVideoBitrateKBPSBoxed(int ordinal) {
        return videoBitrateKBPS;
    }

    public int getVideoPeakBitrateKBPS(int ordinal) {
        if(videoPeakBitrateKBPS == null)
            return Integer.MIN_VALUE;
        return videoPeakBitrateKBPS.intValue();
    }

    public Integer getVideoPeakBitrateKBPSBoxed(int ordinal) {
        return videoPeakBitrateKBPS;
    }

    public long getDashHeaderSize(int ordinal) {
        if(dashHeaderSize == null)
            return Long.MIN_VALUE;
        return dashHeaderSize.longValue();
    }

    public Long getDashHeaderSizeBoxed(int ordinal) {
        return dashHeaderSize;
    }

    public long getDashMediaStartByteOffset(int ordinal) {
        if(dashMediaStartByteOffset == null)
            return Long.MIN_VALUE;
        return dashMediaStartByteOffset.longValue();
    }

    public Long getDashMediaStartByteOffsetBoxed(int ordinal) {
        return dashMediaStartByteOffset;
    }

    public int getDashStreamHeaderDataOrdinal(int ordinal) {
        return dashStreamHeaderDataOrdinal;
    }

    public long getVmafScore(int ordinal) {
        if(vmafScore == null)
            return Long.MIN_VALUE;
        return vmafScore.longValue();
    }

    public Long getVmafScoreBoxed(int ordinal) {
        return vmafScore;
    }

    public int getVmafAlgoVersionExp(int ordinal) {
        if(vmafAlgoVersionExp == null)
            return Integer.MIN_VALUE;
        return vmafAlgoVersionExp.intValue();
    }

    public Integer getVmafAlgoVersionExpBoxed(int ordinal) {
        return vmafAlgoVersionExp;
    }

    public int getVmafAlgoVersionLts(int ordinal) {
        if(vmafAlgoVersionLts == null)
            return Integer.MIN_VALUE;
        return vmafAlgoVersionLts.intValue();
    }

    public Integer getVmafAlgoVersionLtsBoxed(int ordinal) {
        return vmafAlgoVersionLts;
    }

    public int getVmafScoreExp(int ordinal) {
        if(vmafScoreExp == null)
            return Integer.MIN_VALUE;
        return vmafScoreExp.intValue();
    }

    public Integer getVmafScoreExpBoxed(int ordinal) {
        return vmafScoreExp;
    }

    public int getVmafScoreLts(int ordinal) {
        if(vmafScoreLts == null)
            return Integer.MIN_VALUE;
        return vmafScoreLts.intValue();
    }

    public Integer getVmafScoreLtsBoxed(int ordinal) {
        return vmafScoreLts;
    }

    public int getVmafplusScoreExp(int ordinal) {
        if(vmafplusScoreExp == null)
            return Integer.MIN_VALUE;
        return vmafplusScoreExp.intValue();
    }

    public Integer getVmafplusScoreExpBoxed(int ordinal) {
        return vmafplusScoreExp;
    }

    public int getVmafplusScoreLts(int ordinal) {
        if(vmafplusScoreLts == null)
            return Integer.MIN_VALUE;
        return vmafplusScoreLts.intValue();
    }

    public Integer getVmafplusScoreLtsBoxed(int ordinal) {
        return vmafplusScoreLts;
    }

    public int getVmafplusPhoneScoreExp(int ordinal) {
        if(vmafplusPhoneScoreExp == null)
            return Integer.MIN_VALUE;
        return vmafplusPhoneScoreExp.intValue();
    }

    public Integer getVmafplusPhoneScoreExpBoxed(int ordinal) {
        return vmafplusPhoneScoreExp;
    }

    public int getVmafplusPhoneScoreLts(int ordinal) {
        if(vmafplusPhoneScoreLts == null)
            return Integer.MIN_VALUE;
        return vmafplusPhoneScoreLts.intValue();
    }

    public Integer getVmafplusPhoneScoreLtsBoxed(int ordinal) {
        return vmafplusPhoneScoreLts;
    }

    public long getScaledPsnrTimesHundred(int ordinal) {
        if(scaledPsnrTimesHundred == null)
            return Long.MIN_VALUE;
        return scaledPsnrTimesHundred.longValue();
    }

    public Long getScaledPsnrTimesHundredBoxed(int ordinal) {
        return scaledPsnrTimesHundred;
    }

    public float getFps(int ordinal) {
        if(fps == null)
            return Float.NaN;
        return fps.floatValue();
    }

    public Float getFpsBoxed(int ordinal) {
        return fps;
    }

    public int getCropParamsOrdinal(int ordinal) {
        return cropParamsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoStreamInfoTypeAPI) typeAPI;
    }

}