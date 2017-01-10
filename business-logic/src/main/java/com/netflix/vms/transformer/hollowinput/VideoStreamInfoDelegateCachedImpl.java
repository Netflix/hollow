package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoStreamInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoStreamInfoDelegate {

    private final Integer videoBitrateKBPS;
    private final Long dashHeaderSize;
    private final Long dashMediaStartByteOffset;
    private final Long vmafScore;
    private final Long scaledPsnrTimesHundred;
    private final Float fps;
   private VideoStreamInfoTypeAPI typeAPI;

    public VideoStreamInfoDelegateCachedImpl(VideoStreamInfoTypeAPI typeAPI, int ordinal) {
        this.videoBitrateKBPS = typeAPI.getVideoBitrateKBPSBoxed(ordinal);
        this.dashHeaderSize = typeAPI.getDashHeaderSizeBoxed(ordinal);
        this.dashMediaStartByteOffset = typeAPI.getDashMediaStartByteOffsetBoxed(ordinal);
        this.vmafScore = typeAPI.getVmafScoreBoxed(ordinal);
        this.scaledPsnrTimesHundred = typeAPI.getScaledPsnrTimesHundredBoxed(ordinal);
        this.fps = typeAPI.getFpsBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getVideoBitrateKBPS(int ordinal) {
        return videoBitrateKBPS.intValue();
    }

    public Integer getVideoBitrateKBPSBoxed(int ordinal) {
        return videoBitrateKBPS;
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

    public long getVmafScore(int ordinal) {
        return vmafScore.longValue();
    }

    public Long getVmafScoreBoxed(int ordinal) {
        return vmafScore;
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