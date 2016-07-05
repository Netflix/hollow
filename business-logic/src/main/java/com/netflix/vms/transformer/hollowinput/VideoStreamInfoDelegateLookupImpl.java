package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

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