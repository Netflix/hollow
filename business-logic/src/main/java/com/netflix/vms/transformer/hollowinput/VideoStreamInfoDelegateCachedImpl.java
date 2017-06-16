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
        return videoBitrateKBPS.intValue();
    }

    public Integer getVideoBitrateKBPSBoxed(int ordinal) {
        return videoBitrateKBPS;
    }

    public int getVideoPeakBitrateKBPS(int ordinal) {
        return videoPeakBitrateKBPS.intValue();
    }

    public Integer getVideoPeakBitrateKBPSBoxed(int ordinal) {
        return videoPeakBitrateKBPS;
    }

    public long getDashHeaderSize(int ordinal) {
        return dashHeaderSize.longValue();
    }

    public Long getDashHeaderSizeBoxed(int ordinal) {
        return dashHeaderSize;
    }

    public long getDashMediaStartByteOffset(int ordinal) {
        return dashMediaStartByteOffset.longValue();
    }

    public Long getDashMediaStartByteOffsetBoxed(int ordinal) {
        return dashMediaStartByteOffset;
    }

    public int getDashStreamHeaderDataOrdinal(int ordinal) {
        return dashStreamHeaderDataOrdinal;
    }

    public long getVmafScore(int ordinal) {
        return vmafScore.longValue();
    }

    public Long getVmafScoreBoxed(int ordinal) {
        return vmafScore;
    }

    public int getVmafAlgoVersionExp(int ordinal) {
        return vmafAlgoVersionExp.intValue();
    }

    public Integer getVmafAlgoVersionExpBoxed(int ordinal) {
        return vmafAlgoVersionExp;
    }

    public int getVmafAlgoVersionLts(int ordinal) {
        return vmafAlgoVersionLts.intValue();
    }

    public Integer getVmafAlgoVersionLtsBoxed(int ordinal) {
        return vmafAlgoVersionLts;
    }

    public int getVmafScoreExp(int ordinal) {
        return vmafScoreExp.intValue();
    }

    public Integer getVmafScoreExpBoxed(int ordinal) {
        return vmafScoreExp;
    }

    public int getVmafScoreLts(int ordinal) {
        return vmafScoreLts.intValue();
    }

    public Integer getVmafScoreLtsBoxed(int ordinal) {
        return vmafScoreLts;
    }

    public int getVmafplusScoreExp(int ordinal) {
        return vmafplusScoreExp.intValue();
    }

    public Integer getVmafplusScoreExpBoxed(int ordinal) {
        return vmafplusScoreExp;
    }

    public int getVmafplusScoreLts(int ordinal) {
        return vmafplusScoreLts.intValue();
    }

    public Integer getVmafplusScoreLtsBoxed(int ordinal) {
        return vmafplusScoreLts;
    }

    public int getVmafplusPhoneScoreExp(int ordinal) {
        return vmafplusPhoneScoreExp.intValue();
    }

    public Integer getVmafplusPhoneScoreExpBoxed(int ordinal) {
        return vmafplusPhoneScoreExp;
    }

    public int getVmafplusPhoneScoreLts(int ordinal) {
        return vmafplusPhoneScoreLts.intValue();
    }

    public Integer getVmafplusPhoneScoreLtsBoxed(int ordinal) {
        return vmafplusPhoneScoreLts;
    }

    public long getScaledPsnrTimesHundred(int ordinal) {
        return scaledPsnrTimesHundred.longValue();
    }

    public Long getScaledPsnrTimesHundredBoxed(int ordinal) {
        return scaledPsnrTimesHundred;
    }

    public float getFps(int ordinal) {
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