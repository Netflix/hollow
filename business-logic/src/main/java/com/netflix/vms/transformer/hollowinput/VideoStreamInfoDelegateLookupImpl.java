package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoStreamInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoStreamInfoDelegate {

    private final VideoStreamInfoTypeAPI typeAPI;

    public VideoStreamInfoDelegateLookupImpl(VideoStreamInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getVideoBitrateKBPS(int ordinal) {
        return typeAPI.getVideoBitrateKBPS(ordinal);
    }

    public Integer getVideoBitrateKBPSBoxed(int ordinal) {
        return typeAPI.getVideoBitrateKBPSBoxed(ordinal);
    }

    public int getVideoPeakBitrateKBPS(int ordinal) {
        return typeAPI.getVideoPeakBitrateKBPS(ordinal);
    }

    public Integer getVideoPeakBitrateKBPSBoxed(int ordinal) {
        return typeAPI.getVideoPeakBitrateKBPSBoxed(ordinal);
    }

    public long getDashHeaderSize(int ordinal) {
        return typeAPI.getDashHeaderSize(ordinal);
    }

    public Long getDashHeaderSizeBoxed(int ordinal) {
        return typeAPI.getDashHeaderSizeBoxed(ordinal);
    }

    public long getDashMediaStartByteOffset(int ordinal) {
        return typeAPI.getDashMediaStartByteOffset(ordinal);
    }

    public Long getDashMediaStartByteOffsetBoxed(int ordinal) {
        return typeAPI.getDashMediaStartByteOffsetBoxed(ordinal);
    }

    public long getVmafScore(int ordinal) {
        return typeAPI.getVmafScore(ordinal);
    }

    public Long getVmafScoreBoxed(int ordinal) {
        return typeAPI.getVmafScoreBoxed(ordinal);
    }

    public int getVmafAlgoVersionExp(int ordinal) {
        return typeAPI.getVmafAlgoVersionExp(ordinal);
    }

    public Integer getVmafAlgoVersionExpBoxed(int ordinal) {
        return typeAPI.getVmafAlgoVersionExpBoxed(ordinal);
    }

    public int getVmafAlgoVersionLts(int ordinal) {
        return typeAPI.getVmafAlgoVersionLts(ordinal);
    }

    public Integer getVmafAlgoVersionLtsBoxed(int ordinal) {
        return typeAPI.getVmafAlgoVersionLtsBoxed(ordinal);
    }

    public int getVmafScoreExp(int ordinal) {
        return typeAPI.getVmafScoreExp(ordinal);
    }

    public Integer getVmafScoreExpBoxed(int ordinal) {
        return typeAPI.getVmafScoreExpBoxed(ordinal);
    }

    public int getVmafScoreLts(int ordinal) {
        return typeAPI.getVmafScoreLts(ordinal);
    }

    public Integer getVmafScoreLtsBoxed(int ordinal) {
        return typeAPI.getVmafScoreLtsBoxed(ordinal);
    }

    public int getVmafplusScoreExp(int ordinal) {
        return typeAPI.getVmafplusScoreExp(ordinal);
    }

    public Integer getVmafplusScoreExpBoxed(int ordinal) {
        return typeAPI.getVmafplusScoreExpBoxed(ordinal);
    }

    public int getVmafplusScoreLts(int ordinal) {
        return typeAPI.getVmafplusScoreLts(ordinal);
    }

    public Integer getVmafplusScoreLtsBoxed(int ordinal) {
        return typeAPI.getVmafplusScoreLtsBoxed(ordinal);
    }

    public int getVmafplusPhoneScoreExp(int ordinal) {
        return typeAPI.getVmafplusPhoneScoreExp(ordinal);
    }

    public Integer getVmafplusPhoneScoreExpBoxed(int ordinal) {
        return typeAPI.getVmafplusPhoneScoreExpBoxed(ordinal);
    }

    public int getVmafplusPhoneScoreLts(int ordinal) {
        return typeAPI.getVmafplusPhoneScoreLts(ordinal);
    }

    public Integer getVmafplusPhoneScoreLtsBoxed(int ordinal) {
        return typeAPI.getVmafplusPhoneScoreLtsBoxed(ordinal);
    }

    public long getScaledPsnrTimesHundred(int ordinal) {
        return typeAPI.getScaledPsnrTimesHundred(ordinal);
    }

    public Long getScaledPsnrTimesHundredBoxed(int ordinal) {
        return typeAPI.getScaledPsnrTimesHundredBoxed(ordinal);
    }

    public float getFps(int ordinal) {
        return typeAPI.getFps(ordinal);
    }

    public Float getFpsBoxed(int ordinal) {
        return typeAPI.getFpsBoxed(ordinal);
    }

    public int getCropParamsOrdinal(int ordinal) {
        return typeAPI.getCropParamsOrdinal(ordinal);
    }

    public VideoStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}